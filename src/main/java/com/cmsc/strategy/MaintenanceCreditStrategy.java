package com.cmsc.strategy;

import com.cmsc.model.TransactionSource;

/**
 * Awards credit in proportion to the maintenance hours a Shop-Tech logs.
 *
 * Maintenance is rated by time rather than by job because the work varies
 * enormously: clearing a nozzle and rebuilding a CNC axis are both one
 * maintenance log, and paying them the same would be unfair.
 *
 * This is the rule Member 2's Schedule Maintenance use case calls into when it
 * credits the Shop-Tech who did the work.
 *
 * @author Annour Mohamed
 */
public class MaintenanceCreditStrategy implements CreditStrategy {

    /** Credit earned for each hour of maintenance logged. */
    public static final double CREDIT_PER_HOUR = 4.0;

    /**
     * {@inheritDoc}
     *
     * @param quantity maintenance hours logged
     * @return the hourly rate multiplied by the hours logged
     */
    @Override
    public double calculateCredit(double quantity) {
        if (quantity <= 0) {
            return 0;
        }

        return CREDIT_PER_HOUR * quantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionSource getSource() {
        return TransactionSource.MAINTENANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String describe() {
        return CREDIT_PER_HOUR + " credits per hour of maintenance logged";
    }
}
