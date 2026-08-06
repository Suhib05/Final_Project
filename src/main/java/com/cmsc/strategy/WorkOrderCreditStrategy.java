package com.cmsc.strategy;

import com.cmsc.model.TransactionSource;

/**
 * Awards a fixed credit for every fabrication job carried to completion,
 * whether it was raised by a member or by an external client.
 *
 * A flat rate per job is the co-op's design choice. A work order is accepted
 * voluntarily from an open queue, so a member who claims one has already judged
 * it worth doing; rating jobs by time would need hours to be tracked per order,
 * which the schema deliberately does not do. Internal and external jobs earn
 * the same credit, which keeps the rule consistent with the decision to treat
 * both identically once they are in the system.
 *
 * @author Annour Mohamed
 */
public class WorkOrderCreditStrategy implements CreditStrategy {

    /** Credit earned for each work order completed. */
    public static final double CREDIT_PER_JOB = 10.0;

    /**
     * {@inheritDoc}
     *
     * @param quantity number of jobs completed, normally one
     * @return the flat rate multiplied by the number of jobs
     */
    @Override
    public double calculateCredit(double quantity) {
        if (quantity <= 0) {
            return 0;
        }

        return CREDIT_PER_JOB * quantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionSource getSource() {
        return TransactionSource.WORK_ORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String describe() {
        return CREDIT_PER_JOB + " credits per work order completed";
    }
}
