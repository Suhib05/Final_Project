package com.cmsc.model;

import java.time.LocalDateTime;

/**
 * A single entry in the credit/debit ledger.
 *
 * Credits and debits are modelled as one class rather than two because they
 * differ only in their {@link TransactionType} and {@link TransactionSource}.
 * Keeping them together means the whole ledger is auditable from one ordered
 * list and the monthly settlement is a sum over a single table.
 *
 * @author Annour Mohamed
 */
public class Transaction {

    private int transactionId;
    private int userId;
    private TransactionType type;
    private double amount;
    private TransactionSource source;
    private Integer referenceId;
    private LocalDateTime transactionDate;

    /**
     * Creates an empty transaction.
     */
    public Transaction() {
    }

    /**
     * Creates a transaction that has not yet been persisted.
     * The identifier and date are assigned when the row is written.
     *
     * @param userId member the entry belongs to
     * @param type credit or debit
     * @param amount value of the entry, always positive
     * @param source what caused the entry
     * @param referenceId row this entry refers to in its source table, or null
     */
    public Transaction(int userId, TransactionType type, double amount,
            TransactionSource source, Integer referenceId) {

        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.source = source;
        this.referenceId = referenceId;
    }

    /**
     * Creates a transaction with complete information.
     *
     * @param transactionId database identifier
     * @param userId member the entry belongs to
     * @param type credit or debit
     * @param amount value of the entry, always positive
     * @param source what caused the entry
     * @param referenceId row this entry refers to in its source table, or null
     * @param transactionDate moment the entry was recorded
     */
    public Transaction(int transactionId, int userId, TransactionType type,
            double amount, TransactionSource source, Integer referenceId,
            LocalDateTime transactionDate) {

        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.source = source;
        this.referenceId = referenceId;
        this.transactionDate = transactionDate;
    }

    /**
     * Reports whether this entry adds to the member's credit balance.
     *
     * @return true when the entry is a credit
     */
    public boolean isCredit() {
        return type == TransactionType.CREDIT;
    }

    /**
     * Returns the amount signed for arithmetic, positive for a credit and
     * negative for a debit, so a balance is the sum over a list of entries.
     *
     * @return the signed amount
     */
    public double getSignedAmount() {
        return isCredit() ? amount : -amount;
    }

    /**
     * Returns the database identifier.
     *
     * @return transaction identifier
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the database identifier.
     *
     * @param transactionId transaction identifier
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Returns the member this entry belongs to.
     *
     * @return user identifier
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the member this entry belongs to.
     *
     * @param userId user identifier
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the direction of the entry.
     *
     * @return credit or debit
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets the direction of the entry.
     *
     * @param type credit or debit
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * Returns the value of the entry.
     *
     * @return amount, always positive
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the value of the entry.
     *
     * @param amount amount, always positive
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Returns what caused the entry.
     *
     * @return transaction source
     */
    public TransactionSource getSource() {
        return source;
    }

    /**
     * Sets what caused the entry.
     *
     * @param source transaction source
     */
    public void setSource(TransactionSource source) {
        this.source = source;
    }

    /**
     * Returns the row this entry refers to in its source table, for example
     * the training or maintenance log that earned the credit.
     *
     * @return reference identifier, or null when there is none
     */
    public Integer getReferenceId() {
        return referenceId;
    }

    /**
     * Sets the row this entry refers to in its source table.
     *
     * @param referenceId reference identifier, or null when there is none
     */
    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * Returns the moment the entry was recorded.
     *
     * @return transaction date and time
     */
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the moment the entry was recorded.
     *
     * @param transactionDate transaction date and time
     */
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Returns a readable summary of the entry, used for logging and testing.
     *
     * @return string describing this transaction
     */
    @Override
    public String toString() {
        return "Transaction{"
                + "id=" + transactionId
                + ", userId=" + userId
                + ", type=" + type
                + ", amount=" + amount
                + ", source=" + source
                + ", date=" + transactionDate
                + '}';
    }
}
