package com.cmsc.dao;

import com.cmsc.adapter.ExternalJobAdapter;
import com.cmsc.adapter.ExternalJobRequest;
import com.cmsc.model.WorkOrder;
import com.cmsc.model.WorkOrderImpl;
import com.cmsc.model.WorkOrderStatus;
import com.cmsc.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of WorkOrderDAO.
 *
 * The DAO never names a concrete work order type when writing, because every
 * job it is handed already satisfies the {@link WorkOrder} interface. When
 * reading, the is_external column decides which implementation to rebuild: an
 * ordinary {@link WorkOrderImpl} for a member's job, or an
 * {@link ExternalJobAdapter} wrapping a reconstructed
 * {@link ExternalJobRequest} for a job that came in through the public form.
 *
 * @author Annour Mohamed
 */
public class WorkOrderDAOImpl implements WorkOrderDAO {

    private static final String INSERT_SQL
            = "INSERT INTO work_orders "
            + "(requester_name, is_external, description, status, "
            + "assigned_shop_tech_id, date_submitted, date_completed) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL
            = "SELECT * FROM work_orders WHERE work_order_id = ?";

    private static final String UPDATE_SQL
            = "UPDATE work_orders SET requester_name = ?, is_external = ?, "
            + "description = ?, status = ?, assigned_shop_tech_id = ?, "
            + "date_submitted = ?, date_completed = ? "
            + "WHERE work_order_id = ?";

    private static final String DELETE_SQL
            = "DELETE FROM work_orders WHERE work_order_id = ?";

    private static final String FIND_ALL_SQL
            = "SELECT * FROM work_orders ORDER BY date_submitted DESC";

    private static final String FIND_OPEN_QUEUE_SQL
            = "SELECT * FROM work_orders WHERE status <> 'COMPLETED' "
            + "ORDER BY date_submitted";

    private static final String FIND_BY_STATUS_SQL
            = "SELECT * FROM work_orders WHERE status = ? "
            + "ORDER BY date_submitted";

    private static final String FIND_BY_SHOP_TECH_SQL
            = "SELECT * FROM work_orders WHERE assigned_shop_tech_id = ? "
            + "ORDER BY date_submitted DESC";

    /**
     * Saves a new job, whatever its origin.
     *
     * @param workOrder job to save
     * @return saved job containing its generated identifier
     */
    @Override
    public WorkOrder save(WorkOrder workOrder) {
        validate(workOrder);

        if (workOrder.getDateSubmitted() == null) {
            workOrder.setDateSubmitted(LocalDateTime.now());
        }
        if (workOrder.getStatus() == null) {
            workOrder.setStatus(WorkOrderStatus.SUBMITTED);
        }

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(statement, workOrder);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    workOrder.setWorkOrderId(keys.getInt(1));
                }
            }

            return workOrder;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save work order", exception);
        }
    }

    /**
     * Finds a job using its database identifier.
     *
     * @param id work order identifier
     * @return matching job, or null when it does not exist
     */
    @Override
    public WorkOrder findById(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapWorkOrder(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to find work order", exception);
        }
    }

    /**
     * Updates an existing job, for example when a Shop-Tech claims it or
     * marks it complete.
     *
     * @param workOrder job containing updated information
     * @return updated job
     */
    @Override
    public WorkOrder update(WorkOrder workOrder) {
        validate(workOrder);

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(UPDATE_SQL)) {

            setParameters(statement, workOrder);
            statement.setInt(8, workOrder.getWorkOrderId());
            statement.executeUpdate();

            return workOrder;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update work order",
                    exception);
        }
    }

    /**
     * Deletes a job.
     *
     * @param id work order identifier
     */
    @Override
    public void delete(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to delete work order",
                    exception);
        }
    }

    /**
     * Returns every job in the system, newest submission first.
     *
     * @return all jobs
     */
    @Override
    public List<WorkOrder> findAll() {
        return queryList(FIND_ALL_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkOrder> findOpenQueue() {
        return queryList(FIND_OPEN_QUEUE_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        List<WorkOrder> workOrders = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_STATUS_SQL)) {

            statement.setString(1, status.name());

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    workOrders.add(mapWorkOrder(results));
                }
            }

            return workOrders;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read work orders by status",
                    exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkOrder> findByShopTech(int shopTechId) {
        List<WorkOrder> workOrders = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_SHOP_TECH_SQL)) {

            statement.setInt(1, shopTechId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    workOrders.add(mapWorkOrder(results));
                }
            }

            return workOrders;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to read work orders for shop-tech", exception);
        }
    }

    /**
     * Runs a query that takes no parameters and returns a list of jobs.
     *
     * @param sql statement to run
     * @return matching jobs
     */
    private List<WorkOrder> queryList(String sql) {
        List<WorkOrder> workOrders = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                workOrders.add(mapWorkOrder(results));
            }

            return workOrders;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read work orders", exception);
        }
    }

    /**
     * Binds the seven writable columns of a job, in schema order. The values
     * are read through the interface, so the same code serves an internal job
     * and an adapted external one.
     *
     * @param statement statement to bind
     * @param workOrder job supplying the values
     * @throws SQLException when a value cannot be bound
     */
    private void setParameters(PreparedStatement statement,
            WorkOrder workOrder) throws SQLException {

        statement.setString(1, workOrder.getRequesterName());
        statement.setBoolean(2, workOrder.isExternal());
        statement.setString(3, workOrder.getDescription());
        statement.setString(4, workOrder.getStatus().name());

        if (workOrder.getAssignedShopTechId() == null) {
            statement.setNull(5, Types.INTEGER);
        } else {
            statement.setInt(5, workOrder.getAssignedShopTechId());
        }

        statement.setTimestamp(6,
                Timestamp.valueOf(workOrder.getDateSubmitted()));

        if (workOrder.getDateCompleted() == null) {
            statement.setNull(7, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(7,
                    Timestamp.valueOf(workOrder.getDateCompleted()));
        }
    }

    /**
     * Rebuilds a job from the current row of a result set, choosing the
     * implementation that matches how the request originally arrived.
     *
     * The external branch reconstructs the client's request from the columns
     * the schema keeps. Only the company name and the job text survive a round
     * trip; the contact address is not stored, which is a deliberate choice
     * recorded in the design.
     *
     * @param results result set positioned on a row
     * @return the job that row represents
     * @throws SQLException when a column cannot be read
     */
    private WorkOrder mapWorkOrder(ResultSet results) throws SQLException {
        int workOrderId = results.getInt("work_order_id");
        String requesterName = results.getString("requester_name");
        String description = results.getString("description");
        WorkOrderStatus status
                = WorkOrderStatus.valueOf(results.getString("status"));

        Integer shopTechId = results.getInt("assigned_shop_tech_id");
        if (results.wasNull()) {
            shopTechId = null;
        }

        Timestamp submitted = results.getTimestamp("date_submitted");
        Timestamp completed = results.getTimestamp("date_completed");

        LocalDateTime dateSubmitted
                = submitted == null ? null : submitted.toLocalDateTime();
        LocalDateTime dateCompleted
                = completed == null ? null : completed.toLocalDateTime();

        if (results.getBoolean("is_external")) {
            ExternalJobRequest request = new ExternalJobRequest();
            request.setCompanyName(requesterName);
            request.setJobDetails(description);
            request.setReceivedAt(dateSubmitted);

            return new ExternalJobAdapter(workOrderId, request, status,
                    shopTechId, dateSubmitted, dateCompleted);
        }

        return new WorkOrderImpl(workOrderId, requesterName, description,
                status, shopTechId, dateSubmitted, dateCompleted);
    }

    /**
     * Rejects jobs that could not be stored correctly.
     *
     * @param workOrder job to check
     */
    private void validate(WorkOrder workOrder) {
        if (workOrder == null) {
            throw new IllegalArgumentException("Work order must not be null");
        }
        if (workOrder.getRequesterName() == null
                || workOrder.getRequesterName().isBlank()) {
            throw new IllegalArgumentException("Requester name is required");
        }
        if (workOrder.getDescription() == null
                || workOrder.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
    }
}
