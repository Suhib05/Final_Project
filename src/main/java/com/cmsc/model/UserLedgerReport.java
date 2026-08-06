package com.cmsc.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A read-only view of one member's ledger, assembled for FR-06 reporting.
 *
 * The class holds no persistence logic and is never written back to the
 * database. It exists so a report page receives one prepared object instead of
 * recalculating totals in the view, which would put business logic in the
 * Presentation layer.
 *
 * @author Annour Mohamed
 */
public class UserLedgerReport {

    private int userId;
    private String userName;
    private UserType userType;
    private double creditTotal;
    private double debitTotal;
    private double outstandingDebit;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private List<Transaction> entries = new ArrayList<>();

    /**
     * Creates an empty report.
     */
    public UserLedgerReport() {
    }

    /**
     * Creates a report for one member over one period.
     *
     * @param user member the report describes
     * @param entries ledger entries falling inside the period
     * @param periodStart start of the period, inclusive
     * @param periodEnd end of the period, exclusive
     */
    public UserLedgerReport(User user, List<Transaction> entries,
            LocalDateTime periodStart, LocalDateTime periodEnd) {

        this.userId = user.getUserId();
        this.userName = user.getName();
        this.userType = user.getUserType();
        this.outstandingDebit = user.getDebitBalance();
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.entries = entries == null ? new ArrayList<>() : entries;

        for (Transaction entry : this.entries) {
            if (entry.isCredit()) {
                creditTotal += entry.getAmount();
            } else {
                debitTotal += entry.getAmount();
            }
        }
    }

    /**
     * Returns the member's net position over the period, credits less debits.
     *
     * @return positive when the member earned more than they used
     */
    public double getNetPosition() {
        return creditTotal - debitTotal;
    }

    /**
     * Reports whether the member has a debit left to settle.
     *
     * @return true when something is still owed
     */
    public boolean isSettlementDue() {
        return outstandingDebit > 0;
    }

    /**
     * Returns how much of the outstanding debit the member's accumulated
     * credits can cover, which is what the settlement page offers first.
     *
     * @param creditBalance the member's current credit balance
     * @return the amount credits can clear
     */
    public double getCreditsApplicable(double creditBalance) {
        return Math.min(creditBalance, outstandingDebit);
    }

    /**
     * Returns the member identifier.
     *
     * @return user identifier
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the member identifier.
     *
     * @param userId user identifier
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the member's name.
     *
     * @return user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the member's name.
     *
     * @param userName user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the member's role, which decides what the report shows.
     *
     * @return user type
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Sets the member's role.
     *
     * @param userType user type
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Returns the credits earned over the period.
     *
     * @return total credited
     */
    public double getCreditTotal() {
        return creditTotal;
    }

    /**
     * Sets the credits earned over the period.
     *
     * @param creditTotal total credited
     */
    public void setCreditTotal(double creditTotal) {
        this.creditTotal = creditTotal;
    }

    /**
     * Returns the debits incurred over the period.
     *
     * @return total debited
     */
    public double getDebitTotal() {
        return debitTotal;
    }

    /**
     * Sets the debits incurred over the period.
     *
     * @param debitTotal total debited
     */
    public void setDebitTotal(double debitTotal) {
        this.debitTotal = debitTotal;
    }

    /**
     * Returns the balance still owed at the time the report was built.
     *
     * @return outstanding debit
     */
    public double getOutstandingDebit() {
        return outstandingDebit;
    }

    /**
     * Sets the balance still owed.
     *
     * @param outstandingDebit outstanding debit
     */
    public void setOutstandingDebit(double outstandingDebit) {
        this.outstandingDebit = outstandingDebit;
    }

    /**
     * Returns the start of the reporting period.
     *
     * @return period start, inclusive
     */
    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    /**
     * Sets the start of the reporting period.
     *
     * @param periodStart period start, inclusive
     */
    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    /**
     * Returns the end of the reporting period.
     *
     * @return period end, exclusive
     */
    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    /**
     * Sets the end of the reporting period.
     *
     * @param periodEnd period end, exclusive
     */
    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    /**
     * Returns the ledger entries the report was built from.
     *
     * @return entries falling inside the period
     */
    public List<Transaction> getEntries() {
        return entries;
    }

    /**
     * Sets the ledger entries the report was built from.
     *
     * @param entries entries falling inside the period
     */
    public void setEntries(List<Transaction> entries) {
        this.entries = entries == null ? new ArrayList<>() : entries;
    }
}
