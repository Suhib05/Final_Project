package com.cmsc.adapter;

import com.cmsc.model.WorkOrder;
import com.cmsc.model.WorkOrderStatus;

import java.time.LocalDateTime;

/**
 * Presents an {@link ExternalJobRequest} through the internal
 * {@link WorkOrder} interface.
 *
 * This is the Adapter of the Adapter pattern. It holds one external request and
 * maps its fields onto the interface operations: the client's company name
 * answers {@code getRequesterName()} and the free-text block answers
 * {@code getDescription()}. Lifecycle state that the external format has no
 * concept of, such as the queue status and the assigned Shop-Tech, is held by
 * the adapter itself, so the wrapped request is never altered.
 *
 * Because of this class, WorkOrderServlet, the DAO layer and the crediting
 * logic are written once against {@code WorkOrder} and cannot tell an external
 * job from an internal one.
 *
 * @author Annour Mohamed
 */
public class ExternalJobAdapter implements WorkOrder {

    private final ExternalJobRequest request;

    private int workOrderId;
    private WorkOrderStatus status;
    private Integer assignedShopTechId;
    private LocalDateTime dateSubmitted;
    private LocalDateTime dateCompleted;

    /**
     * Wraps a freshly submitted external request. It enters the queue as
     * SUBMITTED with no Shop-Tech assigned.
     *
     * @param request external request to adapt, must not be null
     */
    public ExternalJobAdapter(ExternalJobRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "External job request must not be null");
        }

        this.request = request;
        this.status = WorkOrderStatus.SUBMITTED;
        this.dateSubmitted = request.getReceivedAt() != null
                ? request.getReceivedAt()
                : LocalDateTime.now();
    }

    /**
     * Wraps an external request with lifecycle state already known, used when
     * rebuilding a row read back from the work_orders table.
     *
     * @param workOrderId database identifier
     * @param request external request to adapt, must not be null
     * @param status current position in the job lifecycle
     * @param assignedShopTechId shop-tech who claimed it, or null
     * @param dateSubmitted submission time
     * @param dateCompleted completion time, or null while still open
     */
    public ExternalJobAdapter(int workOrderId, ExternalJobRequest request,
            WorkOrderStatus status, Integer assignedShopTechId,
            LocalDateTime dateSubmitted, LocalDateTime dateCompleted) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "External job request must not be null");
        }

        this.workOrderId = workOrderId;
        this.request = request;
        this.status = status;
        this.assignedShopTechId = assignedShopTechId;
        this.dateSubmitted = dateSubmitted;
        this.dateCompleted = dateCompleted;
    }

    /**
     * Returns the external request being adapted. Exposed so the servlet can
     * reach client-only details such as the contact address, which the
     * internal interface has no place for.
     *
     * @return the wrapped external request
     */
    public ExternalJobRequest getRequest() {
        return request;
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
     *
     * @return the client's company name
     */
    @Override
    public String getRequesterName() {
        return request.getCompanyName();
    }

    /**
     * {@inheritDoc}
     *
     * @return always true, since this class only wraps external submissions
     */
    @Override
    public boolean isExternal() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return the free-text job details supplied by the client
     */
    @Override
    public String getDescription() {
        return request.getJobDetails();
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
     * Returns a readable summary of the adapted job, used for logging and
     * testing.
     *
     * @return string describing this external work order
     */
    @Override
    public String toString() {
        return "ExternalJobAdapter{"
                + "id=" + workOrderId
                + ", client='" + getRequesterName() + '\''
                + ", status=" + status
                + ", assignedTo=" + assignedShopTechId
                + '}';
    }
}
