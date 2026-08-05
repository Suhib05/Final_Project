package com.cmsc.dao;

import com.cmsc.model.MaintenanceLog;
import com.cmsc.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of MaintenanceDAO.
 *
 * @author Saleh Alhajj
 */
public class MaintenanceDAOImpl implements MaintenanceDAO {

    private static final String INSERT_SQL
            = "INSERT INTO maintenance_logs "
            + "(equipment_id, shop_tech_id, date_logged, description, "
            + "hours_at_service) VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL
            = "SELECT * FROM maintenance_logs WHERE log_id = ?";

    private static final String UPDATE_SQL
            = "UPDATE maintenance_logs SET equipment_id = ?, "
            + "shop_tech_id = ?, date_logged = ?, description = ?, "
            + "hours_at_service = ? WHERE log_id = ?";

    private static final String DELETE_SQL
            = "DELETE FROM maintenance_logs WHERE log_id = ?";

    private static final String FIND_ALL_SQL
            = "SELECT * FROM maintenance_logs "
            + "ORDER BY date_logged DESC, log_id DESC";

    private static final String FIND_BY_EQUIPMENT_SQL
            = "SELECT * FROM maintenance_logs WHERE equipment_id = ? "
            + "ORDER BY date_logged DESC, log_id DESC";

    private static final String FIND_BY_SHOP_TECH_SQL
            = "SELECT * FROM maintenance_logs WHERE shop_tech_id = ? "
            + "ORDER BY date_logged DESC, log_id DESC";

    private static final String FIND_LATEST_BY_EQUIPMENT_SQL
            = "SELECT * FROM maintenance_logs WHERE equipment_id = ? "
            + "ORDER BY date_logged DESC, log_id DESC LIMIT 1";

    /**
     * Saves a maintenance record.
     *
     * @param maintenanceLog maintenance record to save
     * @return saved record containing its generated identifier
     */
    @Override
    public MaintenanceLog save(MaintenanceLog maintenanceLog) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setMaintenanceParameters(statement, maintenanceLog);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    maintenanceLog.setLogId(keys.getInt(1));
                }
            }

            return maintenanceLog;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to save maintenance record", exception
            );
        }
    }

    /**
     * Finds a maintenance record using its identifier.
     *
     * @param id maintenance log identifier
     * @return matching record, or null when it does not exist
     */
    @Override
    public MaintenanceLog findById(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapMaintenanceLog(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find maintenance record", exception
            );
        }
    }

    /**
     * Updates an existing maintenance record.
     *
     * @param maintenanceLog record containing updated information
     * @return updated maintenance record
     */
    @Override
    public MaintenanceLog update(MaintenanceLog maintenanceLog) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(UPDATE_SQL)) {

            setMaintenanceParameters(statement, maintenanceLog);
            statement.setInt(6, maintenanceLog.getLogId());
            statement.executeUpdate();

            return maintenanceLog;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to update maintenance record", exception
            );
        }
    }

    /**
     * Deletes a maintenance record.
     *
     * @param id maintenance log identifier
     */
    @Override
    public void delete(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to delete maintenance record", exception
            );
        }
    }

    /**
     * Retrieves all maintenance records.
     *
     * @return all maintenance records
     */
    @Override
    public List<MaintenanceLog> findAll() {
        List<MaintenanceLog> logs = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_ALL_SQL);
                ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                logs.add(mapMaintenanceLog(results));
            }

            return logs;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to retrieve maintenance records", exception
            );
        }
    }

    /**
     * Finds maintenance records for equipment.
     *
     * @param equipmentId equipment identifier
     * @return maintenance history for the equipment
     */
    @Override
    public List<MaintenanceLog> findByEquipmentId(int equipmentId) {
        return findListById(FIND_BY_EQUIPMENT_SQL, equipmentId);
    }

    /**
     * Finds maintenance records completed by a Shop-Tech.
     *
     * @param shopTechId Shop-Tech identifier
     * @return maintenance records completed by the Shop-Tech
     */
    @Override
    public List<MaintenanceLog> findByShopTechId(int shopTechId) {
        return findListById(FIND_BY_SHOP_TECH_SQL, shopTechId);
    }

    /**
     * Finds the latest maintenance record for equipment.
     *
     * @param equipmentId equipment identifier
     * @return latest record, or null when none exists
     */
    @Override
    public MaintenanceLog findLatestByEquipment(int equipmentId) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        FIND_LATEST_BY_EQUIPMENT_SQL)) {

            statement.setInt(1, equipmentId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapMaintenanceLog(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find latest maintenance record", exception
            );
        }
    }

    /**
     * Retrieves maintenance records using one integer query parameter.
     */
    private List<MaintenanceLog> findListById(
            String sql, int identifier) {

        List<MaintenanceLog> logs = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, identifier);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    logs.add(mapMaintenanceLog(results));
                }
            }

            return logs;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to retrieve maintenance records", exception
            );
        }
    }

    /**
     * Places maintenance values into an INSERT or UPDATE statement.
     */
    private void setMaintenanceParameters(
            PreparedStatement statement,
            MaintenanceLog maintenanceLog) throws SQLException {

        statement.setInt(1, maintenanceLog.getEquipmentId());
        statement.setInt(2, maintenanceLog.getShopTechId());
        statement.setTimestamp(
                3,
                Timestamp.valueOf(maintenanceLog.getDateLogged())
        );
        statement.setString(4, maintenanceLog.getDescription());
        statement.setDouble(5, maintenanceLog.getHoursAtService());
    }

    /**
     * Converts one database row into a MaintenanceLog object.
     */
    private MaintenanceLog mapMaintenanceLog(ResultSet results)
            throws SQLException {

        Timestamp dateLogged = results.getTimestamp("date_logged");

        return new MaintenanceLog(
                results.getInt("log_id"),
                results.getInt("equipment_id"),
                results.getInt("shop_tech_id"),
                dateLogged.toLocalDateTime(),
                results.getString("description"),
                results.getDouble("hours_at_service")
        );
    }
}