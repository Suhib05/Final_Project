package com.cmsc.strategy;

import com.cmsc.dao.TransactionDAO;
import com.cmsc.dao.TransactionDAOImpl;
import com.cmsc.dao.UserDAO;
import com.cmsc.dao.UserDAOImpl;
import com.cmsc.model.Transaction;
import com.cmsc.model.TransactionSource;
import com.cmsc.model.TransactionType;
import com.cmsc.model.User;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The business-layer context class for the credit/debit ledger.
 *
 * This is the Context of the Strategy pattern. It holds one
 * {@link CreditStrategy} at a time, selects the implementation matching the
 * contribution being recorded, and delegates the calculation to it. The
 * formulas themselves live in the strategy classes, so adding a contribution
 * type means writing one class and registering it here.
 *
 * Everything that changes a member's standing goes through this class:
 * credits earned, debits incurred, and month-end settlement. Keeping that in
 * one place is what makes the balances on the users table and the rows in the
 * transactions table agree with each other.
 *
 * Other modules call into this class rather than writing ledger rows
 * themselves. Member 1's training flow and Member 2's maintenance flow both
 * use {@link #awardCredit(User, TransactionSource, double)}.
 *
 * @author Annour Mohamed
 */
public class LedgerService {

    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;

    /**
     * The registered credit rules. Adding a contribution type is a matter of
     * writing one strategy class and putting it in this map; no existing code
     * changes.
     */
    private final Map<TransactionSource, CreditStrategy> strategies
            = new EnumMap<>(TransactionSource.class);

    /** The rule currently selected. */
    private CreditStrategy strategy;

    /**
     * Creates a ledger service backed by the project's own DAOs.
     */
    public LedgerService() {
        this(new TransactionDAOImpl(), new UserDAOImpl());
    }

    /**
     * Creates a ledger service using the DAOs supplied, so the calculations
     * can be exercised in a test without touching the database.
     *
     * @param transactionDAO ledger data access
     * @param userDAO member data access, used to keep balances in step
     */
    public LedgerService(TransactionDAO transactionDAO, UserDAO userDAO) {
        this.transactionDAO = transactionDAO;
        this.userDAO = userDAO;

        registerStrategy(new TrainingCreditStrategy());
        registerStrategy(new MaintenanceCreditStrategy());
        registerStrategy(new WorkOrderCreditStrategy());
        registerStrategy(new DonationCreditStrategy());
    }

    /**
     * Registers a credit rule under the source it writes entries for.
     *
     * @param creditStrategy rule to register
     */
    public final void registerStrategy(CreditStrategy creditStrategy) {
        strategies.put(creditStrategy.getSource(), creditStrategy);
    }

    /**
     * Returns the rule currently selected.
     *
     * @return the current strategy, or null before one has been chosen
     */
    public CreditStrategy getStrategy() {
        return strategy;
    }

    /**
     * Selects the rule to delegate to.
     *
     * @param strategy rule to use
     */
    public void setStrategy(CreditStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Records a contribution and credits the member for it.
     *
     * This is the entry point the other modules call when an activity that
     * earns credit completes: a training session delivered, maintenance time
     * logged, or a work order finished. The source decides which rule runs.
     *
     * @param user member who made the contribution
     * @param source what the member did
     * @param quantity units of the contribution, interpreted by the rule:
     *        sessions delivered, hours logged, or jobs completed
     * @return the CREDIT entry written to the ledger
     */
    public Transaction awardCredit(User user, TransactionSource source,
            double quantity) {

        return awardCredit(user, source, quantity, null);
    }

    /**
     * Records a contribution and credits the member for it, keeping a
     * reference to the row that caused it.
     *
     * @param user member who made the contribution
     * @param source what the member did
     * @param quantity units of the contribution, interpreted by the rule
     * @param referenceId row in the source table, for example the training or
     *        maintenance log identifier, or null when there is none
     * @return the CREDIT entry written to the ledger
     */
    public Transaction awardCredit(User user, TransactionSource source,
            double quantity, Integer referenceId) {

        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        CreditStrategy selected = strategies.get(source);
        if (selected == null) {
            throw new IllegalArgumentException(
                    "No credit rule registered for source " + source);
        }

        setStrategy(selected);
        double amount = strategy.calculateCredit(quantity);

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Contribution earns no credit; quantity was " + quantity);
        }

        Transaction credit = new Transaction(user.getUserId(),
                TransactionType.CREDIT, amount, source, referenceId);
        transactionDAO.save(credit);

        user.setCreditBalance(user.getCreditBalance() + amount);
        userDAO.update(user);

        return credit;
    }

    /**
     * Records what a member owes for using the space on their own project.
     *
     * The debit is the access hours used multiplied by the equipment's credit
     * rate, plus the quantity of material consumed. The caller supplies those
     * figures because the session and equipment rows belong to Member 2's
     * module; this class does the ledger arithmetic only.
     *
     * @param user member who used the space
     * @param accessHours hours the session ran for
     * @param accessCreditRate the equipment's credit rate per hour
     * @param materialsConsumed quantity of material used
     * @param sessionId session the debit relates to, or null
     * @return the DEBIT entry written to the ledger
     */
    public Transaction recordEquipmentUse(User user, double accessHours,
            double accessCreditRate, double materialsConsumed,
            Integer sessionId) {

        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        double amount = (accessHours * accessCreditRate) + materialsConsumed;

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "A session with no usage cannot be charged");
        }

        Transaction debit = new Transaction(user.getUserId(),
                TransactionType.DEBIT, amount,
                TransactionSource.EQUIPMENT_USE, sessionId);
        transactionDAO.save(debit);

        user.setDebitBalance(user.getDebitBalance() + amount);
        userDAO.update(user);

        return debit;
    }

    /**
     * Settles a member's outstanding debit at month end.
     *
     * Accumulated credits are applied first, as the co-op decided; whatever
     * remains is cleared by the payment supplied. One SETTLEMENT entry records
     * the total amount cleared.
     *
     * @param user member settling their balance
     * @param payment amount paid, which may be zero when credits cover it all
     * @return the SETTLEMENT entry written, or null when nothing was owed
     */
    public Transaction settle(User user, double payment) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }
        if (payment < 0) {
            throw new IllegalArgumentException("Payment cannot be negative");
        }

        double outstanding = user.getDebitBalance();
        if (outstanding <= 0) {
            return null;
        }

        double creditsApplied = Math.min(user.getCreditBalance(), outstanding);
        double cleared = Math.min(outstanding, creditsApplied + payment);

        Transaction settlement = new Transaction(user.getUserId(),
                TransactionType.DEBIT, cleared,
                TransactionSource.SETTLEMENT, null);
        transactionDAO.save(settlement);

        user.setCreditBalance(user.getCreditBalance() - creditsApplied);
        user.setDebitBalance(outstanding - cleared);
        userDAO.update(user);

        return settlement;
    }

    /**
     * Returns a member's net position straight from the ledger, credits less
     * debits. Read from the transactions table rather than the balance columns
     * so the two can be compared when something looks wrong.
     *
     * @param userId member identifier
     * @return positive when the member is in credit, negative when in debit
     */
    public double getBalance(int userId) {
        return transactionDAO.calculateBalance(userId);
    }

    /**
     * Returns a member's full ledger history, newest entry first.
     *
     * @param userId member identifier
     * @return the member's ledger entries
     */
    public List<Transaction> getLedger(int userId) {
        return transactionDAO.findByUserId(userId);
    }

    /**
     * Returns the entries a member accrued inside a period, which is what the
     * monthly statement is built from.
     *
     * @param userId member identifier
     * @param from start of the period, inclusive
     * @param to end of the period, exclusive
     * @return entries recorded inside the period, oldest first
     */
    public List<Transaction> getStatement(int userId, LocalDateTime from,
            LocalDateTime to) {

        return transactionDAO.findByUserBetween(userId, from, to);
    }
}
