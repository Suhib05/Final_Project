package com.cmsc.model;

/**
 * Identifies what caused a ledger entry to be written.
 * Mirrors the {@code source} column of the {@code transactions} table, so the
 * constants must stay in step with the ENUM declared in the schema.
 *
 * @author Annour Mohamed
 */
public enum TransactionSource {

    /** Member donated filament or another consumable to the co-op. */
    DONATION,

    /** Shop-Tech logged maintenance time on a piece of equipment. */
    MAINTENANCE,

    /** Trainer delivered an intro or safety training session. */
    TRAINING,

    /** A fabrication job was completed, internal or external. */
    WORK_ORDER,

    /** Member consumed access hours and materials on their own project. */
    EQUIPMENT_USE,

    /** Month-end settlement of an outstanding debit balance. */
    SETTLEMENT
}
