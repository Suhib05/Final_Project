package com.cmsc.dao;

import com.cmsc.model.CNCMachine;
import com.cmsc.model.Equipment;
import com.cmsc.model.LaserCutter;
import com.cmsc.model.Printer3D;
import com.cmsc.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of EquipmentDAO.
 * All SQL operations use PreparedStatement and DBConnectionUtil.
 *
 * @author Saleh Alhajj
 */
public class EquipmentDAOImpl implements EquipmentDAO {

    private static final String INSERT_SQL
            = "INSERT INTO equipment "
            + "(asset_tag, make, model, category, status, hours_of_use, "
            + "maintenance_threshold, access_credit_rate) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL
            = "SELECT * FROM equipment WHERE equipment_id = ?";

    private static final String UPDATE_SQL
            = "UPDATE equipment SET asset_tag = ?, make = ?, model = ?, "
            + "category = ?, status = ?, hours_of_use = ?, "
            + "maintenance_threshold = ?, access_credit_rate = ? "
            + "WHERE equipment_id = ?";

    private static final String DELETE_SQL
            = "DELETE FROM equipment WHERE equipment_id = ?";

    private static final String FIND_ALL_SQL
            = "SELECT * FROM equipment ORDER BY equipment_id";

    private static final String FIND_BY_ASSET_TAG_SQL
            = "SELECT * FROM equipment WHERE asset_tag = ?";

    private static final String FIND_BY_STATUS_SQL
            = "SELECT * FROM equipment WHERE status = ? "
            + "ORDER BY equipment_id";

    private static final String FIND_REQUIRING_MAINTENANCE_SQL
            = "SELECT * FROM equipment "
            + "WHERE hours_of_use >= maintenance_threshold "
            + "ORDER BY equipment_id";

    /**
     * Saves new equipment in the database.
     *
     * @param equipment equipment to save
     * @return saved equipment containing its generated identifier
     */
    @Override
    public Equipment save(Equipment equipment) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEquipmentParameters(statement, equipment);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    equipment.setEquipmentId(keys.getInt(1));
                }
            }

            return equipment;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save equipment", exception);
        }
    }

    /**
     * Finds equipment using its database identifier.
     *
     * @param id equipment identifier
     * @return matching equipment, or null when it does not exist
     */
    @Override
    public Equipment findById(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapEquipment(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to find equipment", exception);
        }
    }

    /**
     * Updates existing equipment.
     *
     * @param equipment equipment containing the updated information
     * @return updated equipment
     */
    @Override
    public Equipment update(Equipment equipment) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(UPDATE_SQL)) {

            setEquipmentParameters(statement, equipment);
            statement.setInt(9, equipment.getEquipmentId());
            statement.executeUpdate();

            return equipment;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update equipment", exception);
        }
    }

    /**
     * Deletes equipment using its database identifier.
     *
     * @param id equipment identifier
     */
    @Override
    public void delete(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to delete equipment", exception);
        }
    }

    /**
     * Retrieves all equipment.
     *
     * @return all equipment ordered by identifier
     */
    @Override
    public List<Equipment> findAll() {
        List<Equipment> equipmentList = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_ALL_SQL);
                ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                equipmentList.add(mapEquipment(results));
            }

            return equipmentList;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to retrieve equipment", exception);
        }
    }

    /**
     * Finds equipment using its unique asset tag.
     *
     * @param assetTag unique asset tag
     * @return matching equipment, or null when it does not exist
     */
    @Override
    public Equipment findByAssetTag(String assetTag) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ASSET_TAG_SQL)) {

            statement.setString(1, assetTag);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapEquipment(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find equipment by asset tag", exception
            );
        }
    }

    /**
     * Finds equipment with the requested status.
     *
     * @param status equipment status
     * @return matching equipment
     */
    @Override
    public List<Equipment> findByStatus(String status) {
        List<Equipment> equipmentList = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_STATUS_SQL)) {

            statement.setString(1, status);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    equipmentList.add(mapEquipment(results));
                }
            }

            return equipmentList;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find equipment by status", exception
            );
        }
    }

    /**
     * Finds equipment that reached its maintenance threshold.
     *
     * @return equipment requiring maintenance
     */
    @Override
    public List<Equipment> findRequiringMaintenance() {
        List<Equipment> equipmentList = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        FIND_REQUIRING_MAINTENANCE_SQL);
                ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                equipmentList.add(mapEquipment(results));
            }

            return equipmentList;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find equipment requiring maintenance", exception
            );
        }
    }

    /**
     * Places the equipment values into an INSERT or UPDATE statement.
     */
    private void setEquipmentParameters(
            PreparedStatement statement, Equipment equipment)
            throws SQLException {

        statement.setString(1, equipment.getAssetTag());
        statement.setString(2, equipment.getMake());
        statement.setString(3, equipment.getModel());
        statement.setString(4, equipment.getCategory());
        statement.setString(5, equipment.getStatus());
        statement.setDouble(6, equipment.getHoursOfUse());
        statement.setDouble(7, equipment.getMaintenanceThreshold());
        statement.setDouble(8, equipment.getAccessCreditRate());
    }

    /**
     * Converts one equipment database row into its correct subclass.
     */
    private Equipment mapEquipment(ResultSet results) throws SQLException {
        String category = results.getString("category");

        return switch (category) {
            case "PRINTER_3D" ->
                new Printer3D(
                        results.getInt("equipment_id"),
                        results.getString("asset_tag"),
                        results.getString("make"),
                        results.getString("model"),
                        results.getString("status"),
                        results.getDouble("hours_of_use"),
                        results.getDouble("maintenance_threshold"),
                        results.getDouble("access_credit_rate")
                );

            case "LASER_CUTTER" ->
                new LaserCutter(
                        results.getInt("equipment_id"),
                        results.getString("asset_tag"),
                        results.getString("make"),
                        results.getString("model"),
                        results.getString("status"),
                        results.getDouble("hours_of_use"),
                        results.getDouble("maintenance_threshold"),
                        results.getDouble("access_credit_rate")
                );

            case "CNC" ->
                new CNCMachine(
                        results.getInt("equipment_id"),
                        results.getString("asset_tag"),
                        results.getString("make"),
                        results.getString("model"),
                        results.getString("status"),
                        results.getDouble("hours_of_use"),
                        results.getDouble("maintenance_threshold"),
                        results.getDouble("access_credit_rate")
                );

            default ->
                throw new SQLException(
                        "Unknown equipment category: " + category
                );
        };
    }
}