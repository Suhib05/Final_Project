package com.cmsc.dao;

import com.cmsc.model.Transaction;
import com.cmsc.model.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Defines database operations for the credit/debit ledger.
 * Every credit earned, debit incurred and settlement made is one row in the
 * transactions table, so the whole ledger is read back through this interface.
 *
 * @author Annour Mohamed
 */
public interface TransactionDAO extends GenericDAO<Transaction> {

    /**
     * Finds every ledger entry belonging to one member, newest first.
     *
     * @param userId member identifier
     * @return the member's ledger entries
     */
    List<Transaction> findByUserId(int userId);

    /**
     * Finds a member's ledger entries falling inside a period, oldest first.
     * Used to produce the monthly statement for settlement.
     *
     * @param userId member identifier
     * @param from start of the period, inclusive
     * @param to end of the period, exclusive
     * @return entries recorded inside the period
     */
    List<Transaction> findByUserBetween(int userId, LocalDateTime from,
            LocalDateTime to);

    /**
     * Totals one side of a member's ledger.
     *
     * @param userId member identifier
     * @param type credit or debit
     * @return sum of the matching entries, or 0 when there are none
     */
    double sumByType(int userId, TransactionType type);

    /**
     * Returns a member's net position, credits less debits.
     *
     * @param userId member identifier
     * @return positive when the member is in credit, negative when in debit
     */
    double calculateBalance(int userId);
}
