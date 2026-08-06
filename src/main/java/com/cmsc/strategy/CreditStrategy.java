package com.cmsc.strategy;

import com.cmsc.model.TransactionSource;

/**
 * The single operation every credit rule must provide.
 *
 * This interface is the Strategy of the Strategy pattern. Credits are earned in
 * several unrelated ways and the co-op expects to add more over time, so each
 * formula lives in its own class behind this contract. {@link LedgerService}
 * only ever calls {@code calculateCredit} and never knows which formula is
 * running, which means a new contribution type costs one new class and no
 * change to any existing one.
 *
 * @author Annour Mohamed
 */
public interface CreditStrategy {

    /**
     * Works out how much credit a contribution is worth.
     *
     * @param quantity the units the contribution is measured in, which each
     *        rule interprets for itself: training sessions delivered,
     *        maintenance hours logged, or jobs completed
     * @return the credit earned, never negative
     */
    double calculateCredit(double quantity);

    /**
     * Reports which ledger source this rule writes its entries under, so the
     * context does not need a conditional to work it out.
     *
     * @return the transaction source matching this rule
     */
    TransactionSource getSource();

    /**
     * Describes the rule in words, for display on the ledger and report pages.
     *
     * @return a short human-readable description of the formula
     */
    String describe();
}
