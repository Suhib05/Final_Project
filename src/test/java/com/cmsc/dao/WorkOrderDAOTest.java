package com.cmsc.dao;

import com.cmsc.adapter.ExternalJobAdapter;
import com.cmsc.adapter.ExternalJobRequest;
import com.cmsc.model.WorkOrder;
import com.cmsc.model.WorkOrderImpl;
import com.cmsc.model.WorkOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests WorkOrderDAOImpl against the real CMSC database.
 *
 * Covers both origins a job can have, because the point of the Adapter is that
 * the DAO stores them through one interface and still rebuilds each as the
 * right type on the way back out.
 *
 * @author Annour Mohamed
 */
class WorkOrderDAOTest {

    private final WorkOrderDAO workOrderDAO = new WorkOrderDAOImpl();

    /** Carla Diaz from the sample data, a SHOP_TECH. */
    private static final int TEST_SHOP_TECH_ID = 3;

    @Test
    void savesFindsUpdatesAndDeletesInternalWorkOrder() {
        WorkOrder job = new WorkOrderImpl(
                "Alice Chen",
                "Print a replacement bracket for the lab bench"
        );

        WorkOrder saved = workOrderDAO.save(job);
        int generatedId = saved.getWorkOrderId();

        assertTrue(generatedId > 0);

        try {
            WorkOrder found = workOrderDAO.findById(generatedId);

            assertNotNull(found);
            assertInstanceOf(WorkOrderImpl.class, found);
            assertFalse(found.isExternal());
            assertEquals("Alice Chen", found.getRequesterName());
            assertEquals(WorkOrderStatus.SUBMITTED, found.getStatus());
            assertNull(found.getAssignedShopTechId());
            assertNull(found.getDateCompleted());

            List<WorkOrder> queue = workOrderDAO.findOpenQueue();

            assertTrue(queue.stream()
                    .anyMatch(order
                            -> order.getWorkOrderId() == generatedId));

            found.setStatus(WorkOrderStatus.COMPLETED);
            found.setAssignedShopTechId(TEST_SHOP_TECH_ID);
            found.setDateCompleted(LocalDateTime.now().withNano(0));
            workOrderDAO.update(found);

            WorkOrder updated = workOrderDAO.findById(generatedId);

            assertNotNull(updated);
            assertEquals(WorkOrderStatus.COMPLETED, updated.getStatus());
            assertEquals(TEST_SHOP_TECH_ID, updated.getAssignedShopTechId());
            assertNotNull(updated.getDateCompleted());

            assertTrue(workOrderDAO.findByShopTech(TEST_SHOP_TECH_ID).stream()
                    .anyMatch(order
                            -> order.getWorkOrderId() == generatedId));

            assertFalse(workOrderDAO.findOpenQueue().stream()
                    .anyMatch(order
                            -> order.getWorkOrderId() == generatedId));
        } finally {
            workOrderDAO.delete(generatedId);
        }

        assertNull(workOrderDAO.findById(generatedId));
    }

    @Test
    void storesAnAdaptedExternalRequestAndRebuildsItAsAnAdapter() {
        ExternalJobRequest request = new ExternalJobRequest(
                "Bytown Robotics",
                "orders@bytownrobotics.ca",
                "Laser cut 20 acrylic panels, drawings attached"
        );

        WorkOrder job = new ExternalJobAdapter(request);

        assertTrue(job.isExternal());
        assertEquals("Bytown Robotics", job.getRequesterName());

        WorkOrder saved = workOrderDAO.save(job);
        int generatedId = saved.getWorkOrderId();

        assertTrue(generatedId > 0);

        try {
            WorkOrder found = workOrderDAO.findById(generatedId);

            assertNotNull(found);
            assertInstanceOf(ExternalJobAdapter.class, found);
            assertTrue(found.isExternal());
            assertEquals("Bytown Robotics", found.getRequesterName());
            assertEquals(
                    "Laser cut 20 acrylic panels, drawings attached",
                    found.getDescription()
            );
            assertEquals(WorkOrderStatus.SUBMITTED, found.getStatus());

            List<WorkOrder> submitted
                    = workOrderDAO.findByStatus(WorkOrderStatus.SUBMITTED);

            assertTrue(submitted.stream()
                    .anyMatch(order
                            -> order.getWorkOrderId() == generatedId));
        } finally {
            workOrderDAO.delete(generatedId);
        }

        assertNull(workOrderDAO.findById(generatedId));
    }

    @Test
    void queueIsOrderedBySubmissionTime() {
        List<WorkOrder> queue = workOrderDAO.findOpenQueue();

        for (int i = 1; i < queue.size(); i++) {
            LocalDateTime earlier = queue.get(i - 1).getDateSubmitted();
            LocalDateTime later = queue.get(i).getDateSubmitted();

            assertFalse(earlier.isAfter(later),
                    "Open queue must be first come, first served");
        }
    }
}
