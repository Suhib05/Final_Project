package com.cmsc.model;

import java.time.LocalDateTime;

/**
 * The internal contract every fabrication job satisfies, regardless of origin.
 *
 * This interface is the Target of the Adapter pattern. Work orders raised by a
 * registered member are {@link WorkOrderImpl} instances; requests arriving from
 * an external client are wrapped by an adapter that presents them through this
 * same interface. Servlets, the DAO layer and the crediting logic depend only
 * on this type and therefore cannot tell the two sources apart.
 *
 * @author Annour Mohamed
 */
public interface WorkOrder {

    /**
     * Returns the database identifier.
     *
     * @return work order identifier, or 0 before the row is written
     */
    int getWorkOrderId();

    /**
     * Sets the database identifier once the row has been written.
     *
     * @param workOrderId work order identifier
     */
    void setWorkOrderId(int workOrderId);

    /**
     * Returns the name shown to the Shop-Tech as the requester. For an
     * internal job this is the member's name; for an external job the
     * adapter supplies the client's company name.
     *
     * @return requester name
     */
    String getRequesterName();

    /**
     * Reports whether the request came from outside the co-op.
     *
     * @return true when the request originated with an external client
     */
    boolean isExternal();

    /**
     * Returns the description of the work to be carried out.
     *
     * @return job description
     */
    String getDescription();

    /**
     * Returns the current position in the job lifecycle.
     *
     * @return work order status
     */
    WorkOrderStatus getStatus();

    /**
     * Moves the job to a new position in its lifecycle.
     *
     * @param status new work order status
     */
    void setStatus(WorkOrderStatus status);

    /**
     * Returns the Shop-Tech who claimed the job.
     *
     * @return shop-tech user identifier, or null while the job is unclaimed
     */
    Integer getAssignedShopTechId();

    /**
     * Assigns the job to a Shop-Tech.
     *
     * @param assignedShopTechId shop-tech user identifier, or null to unassign
     */
    void setAssignedShopTechId(Integer assignedShopTechId);

    /**
     * Returns the submission time, which is what orders the open queue.
     *
     * @return date and time the request was submitted
     */
    LocalDateTime getDateSubmitted();

    /**
     * Sets the submission time.
     *
     * @param dateSubmitted date and time the request was submitted
     */
    void setDateSubmitted(LocalDateTime dateSubmitted);

    /**
     * Returns the completion time.
     *
     * @return date and time the job was completed, or null while it is open
     */
    LocalDateTime getDateCompleted();

    /**
     * Sets the completion time.
     *
     * @param dateCompleted date and time the job was completed
     */
    void setDateCompleted(LocalDateTime dateCompleted);
}
