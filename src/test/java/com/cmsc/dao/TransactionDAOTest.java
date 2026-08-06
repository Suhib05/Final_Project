package com.cmsc.dao;

import com.cmsc.model.Transaction;
import com.cmsc.model.TransactionSource;
import com.cmsc.model.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests TransactionDAOImpl against the real CMSC database.
 *
 * @author Annour Mohamed
 */
class TransactionDAOTest {

    private final TransactionDAO transactionDAO = new TransactionDAOImpl();

    /** Alice Chen from the sample data. */
    private static final int TEST_USER_ID = 1;

    @Test
    void savesFindsUpdatesAndDeletesTransaction() {
        Transaction credit = new Transaction(
                TEST_USER_ID,
                TransactionType.CREDIT,
                15.0,
                TransactionSource.TRAINING,
                null
        );

        Transaction saved = transactionDAO.save(credit);
        int generatedId = saved.getTransactionId();

        assertTrue(generatedId > 0);
        assertNotNull(saved.getTransactionDate());

        try {
            Transaction found = transactionDAO.findById(generatedId);

            assertNotNull(found);
            assertEquals(TEST_USER_ID, found.getUserId());
            assertEquals(TransactionType.CREDIT, found.getType());
            assertEquals(TransactionSource.TRAINING, found.getSource());
            assertEquals(15.0, found.getAmount(), 0.001);
            assertNull(found.getReferenceId());
            assertTrue(found.isCredit());

            List<Transaction> userEntries
                    = transactionDAO.findByUserId(TEST_USER_ID);

            assertTrue(userEntries.stream()
                    .anyMatch(entry
                            -> entry.getTransactionId() == generatedId));

            List<Transaction> inPeriod = transactionDAO.findByUserBetween(
                    TEST_USER_ID,
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now().plusDays(1)
            );

            assertTrue(inPeriod.stream()
                    .anyMatch(entry
                            -> entry.getTransactionId() == generatedId));

            saved.setAmount(20.0);
            saved.setReferenceId(1);
            transactionDAO.update(saved);

            Transaction updated = transactionDAO.findById(generatedId);

            assertNotNull(updated);
            assertEquals(20.0, updated.getAmount(), 0.001);
            assertEquals(1, updated.getReferenceId());
        } finally {
            transactionDAO.delete(generatedId);
        }

        assertNull(transactionDAO.findById(generatedId));
    }

    @Test
    void totalsCreditsAndDebitsIntoABalance() {
        double balanceBefore = transactionDAO.calculateBalance(TEST_USER_ID);

        Transaction credit = transactionDAO.save(new Transaction(
                TEST_USER_ID, TransactionType.CREDIT, 30.0,
                TransactionSource.MAINTENANCE, null));

        Transaction debit = transactionDAO.save(new Transaction(
                TEST_USER_ID, TransactionType.DEBIT, 10.0,
                TransactionSource.EQUIPMENT_USE, null));

        try {
            double balanceAfter
                    = transactionDAO.calculateBalance(TEST_USER_ID);

            assertEquals(balanceBefore + 20.0, balanceAfter, 0.001);

            assertTrue(transactionDAO.sumByType(
                    TEST_USER_ID, TransactionType.CREDIT) >= 30.0);
            assertTrue(transactionDAO.sumByType(
                    TEST_USER_ID, TransactionType.DEBIT) >= 10.0);
        } finally {
            transactionDAO.delete(credit.getTransactionId());
            transactionDAO.delete(debit.getTransactionId());
        }
    }

    @Test
    void refusesEntriesThatWouldCorruptTheLedger() {
        assertThrows(IllegalArgumentException.class,
                () -> transactionDAO.save(new Transaction(
                        TEST_USER_ID, TransactionType.CREDIT, 0.0,
                        TransactionSource.TRAINING, null)));

        assertThrows(IllegalArgumentException.class,
                () -> transactionDAO.save(new Transaction(
                        TEST_USER_ID, TransactionType.CREDIT, -5.0,
                        TransactionSource.TRAINING, null)));

        assertThrows(IllegalArgumentException.class,
                () -> transactionDAO.save(new Transaction(
                        TEST_USER_ID, null, 5.0,
                        TransactionSource.TRAINING, null)));
    }
}
