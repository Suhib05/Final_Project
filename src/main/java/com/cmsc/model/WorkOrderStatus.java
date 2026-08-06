package com.cmsc.model;

/**
 * Lifecycle of a fabrication job.
 * Mirrors the {@code status} column of the {@code work_orders} table.
 *
 * @author Annour Mohamed
 */
public enum WorkOrderStatus {

    /** Submitted and waiting in the open queue. */
    SUBMITTED,

    /** Claimed by a Shop-Tech and being worked on. */
    IN_PROGRESS,

    /** Finished; the Shop-Tech who completed it earns credit. */
    COMPLETED
}
