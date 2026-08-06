package com.cmsc.dao;

import com.cmsc.model.WorkOrder;
import com.cmsc.model.WorkOrderStatus;

import java.util.List;

/**
 * Defines database operations for fabrication jobs.
 * The DAO works entirely through the {@link WorkOrder} interface, so internal
 * jobs and adapted external requests are stored and read back the same way.
 *
 * @author Annour Mohamed
 */
public interface WorkOrderDAO extends GenericDAO<WorkOrder> {

    /**
     * Returns the open queue: every job not yet completed, ordered by
     * submission time, which is the first come first served rule the co-op
     * decided on.
     *
     * @return open jobs, oldest submission first
     */
    List<WorkOrder> findOpenQueue();

    /**
     * Finds every job at one point in its lifecycle.
     *
     * @param status status to match
     * @return matching jobs, oldest submission first
     */
    List<WorkOrder> findByStatus(WorkOrderStatus status);

    /**
     * Finds every job claimed by one Shop-Tech.
     *
     * @param shopTechId shop-tech user identifier
     * @return jobs assigned to that Shop-Tech, newest submission first
     */
    List<WorkOrder> findByShopTech(int shopTechId);
}
