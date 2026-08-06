package com.cmsc.model;

import java.time.LocalDateTime;

/**
 * A fabrication job raised by a registered member of the co-op.
 *
 * This is the ordinary implementation of {@link WorkOrder}: it stores the
 * requesting member's name directly and always reports itself as internal.
 * External requests never use this class; they reach the system through the
 * adapter instead.
 *
 * @author Annour Mohamed
 */
public class WorkOrderImpl implements WorkOrder {

    private int workOrderId;
    private String requesterName;
    private String description;
    private WorkOrderStatus status;
    private Integer assignedShopTechId;
    private LocalDateTime dateSubmitted;
    private LocalDateTime dateCompleted;

    /**
     * Creates an empty work order.
     */
    public WorkOrderImpl() {
        this.status = WorkOrderStatus.SUBMITTED;
    }

    /**
     * Creates a work order that has not yet been persisted. It enters the
     * queue as SUBMITTED with no Shop-Tech assigned.
     *
     * @param requesterName name of the member raising the job
     * @param description work to be carried out
     */
    public WorkOrderImpl(String requesterName, String description) {
        this.requesterName = requesterName;
        this.description = description;
        this.status = WorkOrderStatus.SUBMITTED;
        this.dateSubmitted = LocalDateTime.now();
    }

    /**
     * Creates a work order with complete information, used when rebuilding a
     * row read back from the database.
     *
     * @param workOrderId database identifier
     * @param requesterName name of the member raising the job
     * @param description work to be carried out
     * @param status current position in the job lifecycle
     * @param assignedShopTechId shop-tech who claimed it, or null
     * @param dateSubmitted submission time
     * @param dateCompleted completion time, or null while still open
     */
    public WorkOrderImpl(int workOrderId, String requesterName,
            String description, WorkOrderStatus status,
            Integer assignedShopTechId, LocalDateTime dateSubmitted,
            LocalDateTime dateCompleted) {

        this.workOrderId = workOrderId;
        this.requesterName = requesterName;
        this.description = description;
        this.status = status;
        this.assignedShopTechId = assignedShopTechId;
        this.dateSubmitted = dateSubmitted;
        this.dateCompleted = dateCompleted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWorkOrderId() {
        return workOrderId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequesterName() {
        return requesterName;
    }

    /**
     * Sets the name of the member raising the job.
     *
     * @param requesterName requester name
     */
    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    /**
     * {@inheritDoc}
     *
     * @return always false, since this class only represents internal jobs
     */
    @Override
    public boolean isExternal() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the work to be carried out.
     *
     * @param description job description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkOrderStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getAssignedShopTechId() {
        return assignedShopTechId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAssignedShopTechId(Integer assignedShopTechId) {
        this.assignedShopTechId = assignedShopTechId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateSubmitted(LocalDateTime dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getDateCompleted() {
        return dateCompleted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateCompleted(LocalDateTime dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    /**
     * Returns a readable summary of the job, used for logging and testing.
     *
     * @return string describing this work order
     */
    @Override
    public String toString() {
        return "WorkOrderImpl{"
                + "id=" + workOrderId
                + ", requester='" + requesterName + '\''
                + ", status=" + status
                + ", assignedTo=" + assignedShopTechId
                + '}';
    }
}
