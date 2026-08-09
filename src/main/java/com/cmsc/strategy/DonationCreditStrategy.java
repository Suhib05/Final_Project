package com.cmsc.strategy;

import com.cmsc.model.TransactionSource;

/**
 * Awards credit in proportion to the quantity of consumable material a member
 * donates to the co-op.
 *
 * Donations are rated by quantity because that is what the co-op receives: a
 * kilogram of filament is worth the same whoever hands it over, and the member
 * is credited for the value they added to the shared stock rather than for
 * any time spent.
 *
 * This class is the fourth credit rule, added after the first three were
 * already working. It required no change to {@link CreditStrategy},
 * {@link LedgerService} or any existing rule, which is the property the
 * Strategy pattern was chosen for.
 *
 * @author Annour Mohamed
 */
public class DonationCreditStrategy implements CreditStrategy {

    /** Credit earned for each unit of material donated. */
    public static final double CREDIT_PER_UNIT = 0.05;

    /**
     * {@inheritDoc}
     *
     * @param quantity units of material donated, in the consumable's own unit
     * @return the unit rate multiplied by the quantity donated
     */
    @Override
    public double calculateCredit(double quantity) {
        if (quantity <= 0) {
            return 0;
        }

        return CREDIT_PER_UNIT * quantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionSource getSource() {
        return TransactionSource.DONATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String describe() {
        return CREDIT_PER_UNIT + " credits per unit of material donated";
    }
}
