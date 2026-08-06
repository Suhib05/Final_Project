package com.cmsc.strategy;

import com.cmsc.model.TransactionSource;

/**
 * Awards a fixed credit for every intro or safety training session a Trainer
 * delivers.
 *
 * A flat rate is used because a training session is a defined unit of work: the
 * co-op cares that the session was delivered, not how long the Trainer spent
 * on it.
 *
 * @author Annour Mohamed
 */
public class TrainingCreditStrategy implements CreditStrategy {

    /** Credit earned for each training session delivered. */
    public static final double CREDIT_PER_SESSION = 5.0;

    /**
     * {@inheritDoc}
     *
     * @param quantity number of training sessions delivered
     * @return the flat rate multiplied by the number of sessions
     */
    @Override
    public double calculateCredit(double quantity) {
        if (quantity <= 0) {
            return 0;
        }

        return CREDIT_PER_SESSION * quantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionSource getSource() {
        return TransactionSource.TRAINING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String describe() {
        return CREDIT_PER_SESSION + " credits per training session delivered";
    }
}
