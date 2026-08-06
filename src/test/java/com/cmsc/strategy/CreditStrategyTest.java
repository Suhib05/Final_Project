package com.cmsc.strategy;

import com.cmsc.model.TransactionSource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the credit formulas on their own, without touching the database.
 *
 * Each rule is exercised through the {@link CreditStrategy} interface, which is
 * the point of the pattern: the caller works with the contract and never names
 * a concrete formula.
 *
 * @author Annour Mohamed
 */
class CreditStrategyTest {

    @Test
    void trainingEarnsAFlatCreditPerSession() {
        CreditStrategy strategy = new TrainingCreditStrategy();

        assertEquals(TransactionSource.TRAINING, strategy.getSource());
        assertEquals(TrainingCreditStrategy.CREDIT_PER_SESSION,
                strategy.calculateCredit(1), 0.001);
        assertEquals(TrainingCreditStrategy.CREDIT_PER_SESSION * 3,
                strategy.calculateCredit(3), 0.001);
    }

    @Test
    void maintenanceEarnsCreditInProportionToHours() {
        CreditStrategy strategy = new MaintenanceCreditStrategy();

        assertEquals(TransactionSource.MAINTENANCE, strategy.getSource());
        assertEquals(MaintenanceCreditStrategy.CREDIT_PER_HOUR * 2.5,
                strategy.calculateCredit(2.5), 0.001);
    }

    @Test
    void workOrderEarnsAFlatCreditPerCompletedJob() {
        CreditStrategy strategy = new WorkOrderCreditStrategy();

        assertEquals(TransactionSource.WORK_ORDER, strategy.getSource());
        assertEquals(WorkOrderCreditStrategy.CREDIT_PER_JOB,
                strategy.calculateCredit(1), 0.001);
    }

    @Test
    void noContributionEarnsNoCredit() {
        assertEquals(0, new TrainingCreditStrategy().calculateCredit(0), 0.001);
        assertEquals(0,
                new MaintenanceCreditStrategy().calculateCredit(0), 0.001);
        assertEquals(0,
                new WorkOrderCreditStrategy().calculateCredit(-1), 0.001);
    }

    @Test
    void everyRuleDescribesItselfForTheLedgerPage() {
        CreditStrategy[] rules = {
            new TrainingCreditStrategy(),
            new MaintenanceCreditStrategy(),
            new WorkOrderCreditStrategy()
        };

        for (CreditStrategy rule : rules) {
            assertNotNull(rule.describe());
            assertNotNull(rule.getSource());
        }
    }
}
