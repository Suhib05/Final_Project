package com.cmsc.model;

/**
 * Distinguishes the two directions a ledger entry can take.
 * Mirrors the {@code type} column of the {@code transactions} table.
 *
 * @author Annour Mohamed
 */
public enum TransactionType {

    /** Value earned by a member through a contribution to the co-op. */
    CREDIT,

    /** Value owed by a member for access hours or materials consumed. */
    DEBIT
}
