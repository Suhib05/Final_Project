package com.cmsc.dao;

import com.cmsc.model.Consumable;
import com.cmsc.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of ConsumableDAO.
 *
 * @author Saleh Alhajj
 */
public class ConsumableDAOImpl implements ConsumableDAO {

    private static final String INSERT_SQL
            = "INSERT INTO consumables "
            + "(type, unit, stock_level, consumption_rate, "
            + "reorder_threshold) VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL
            = "SELECT * FROM consumables WHERE consumable_id = ?";

    private static final String UPDATE_SQL
            = "UPDATE consumables SET type = ?, unit = ?, "
            + "stock_level = ?, consumption_rate = ?, "
            + "reorder_threshold = ? WHERE consumable_id = ?";

    private static final String DELETE_SQL
            = "DELETE FROM consumables WHERE consumable_id = ?";

    private static final String FIND_ALL_SQL
            = "SELECT * FROM consumables ORDER BY consumable_id";

    private static final String FIND_BY_TYPE_SQL
            = "SELECT * FROM consumables WHERE type = ? "
            + "ORDER BY consumable_id LIMIT 1";

    private static final String FIND_LOW_STOCK_SQL
            = "SELECT * FROM consumables "
            + "WHERE stock_level <= reorder_threshold "
            + "ORDER BY consumable_id";

    /**
     * Saves a new consumable.
     *
     * @param consumable consumable to save
     * @return saved consumable containing its generated identifier
     */
    @Override
    public Consumable save(Consumable consumable) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setConsumableParameters(statement, consumable);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    consumable.setConsumableId(keys.getInt(1));
                }
            }

            return consumable;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save consumable", exception);
        }
    }

    /**
     * Finds a consumable using its database identifier.
     *
     * @param id consumable identifier
     * @return matching consumable, or null when it does not exist
     */
    @Override
    public Consumable findById(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapConsumable(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to find consumable", exception);
        }
    }

    /**
     * Updates an existing consumable.
     *
     * @param consumable consumable containing updated information
     * @return updated consumable
     */
    @Override
    public Consumable update(Consumable consumable) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(UPDATE_SQL)) {

            setConsumableParameters(statement, consumable);
            statement.setInt(6, consumable.getConsumableId());
            statement.executeUpdate();

            return consumable;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to update consumable", exception
            );
        }
    }

    /**
     * Deletes a consumable using its database identifier.
     *
     * @param id consumable identifier
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
                    "Unable to delete consumable", exception
            );
        }
    }

    /**
     * Retrieves all consumables.
     *
     * @return all consumables ordered by identifier
     */
    @Override
    public List<Consumable> findAll() {
        List<Consumable> consumables = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_ALL_SQL);
                ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                consumables.add(mapConsumable(results));
            }

            return consumables;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to retrieve consumables", exception
            );
        }
    }

    /**
     * Finds a consumable using its type.
     *
     * @param type consumable type
     * @return matching consumable, or null when none exists
     */
    @Override
    public Consumable findByType(String type) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_TYPE_SQL)) {

            statement.setString(1, type);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapConsumable(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find consumable by type", exception
            );
        }
    }

    /**
     * Finds all consumables at or below their reorder thresholds.
     *
     * @return low-stock consumables
     */
    @Override
    public List<Consumable> findLowStock() {
        List<Consumable> consumables = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_LOW_STOCK_SQL);
                ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                consumables.add(mapConsumable(results));
            }

            return consumables;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to retrieve low-stock consumables", exception
            );
        }
    }

    /**
     * Places consumable values into an INSERT or UPDATE statement.
     */
    private void setConsumableParameters(
            PreparedStatement statement, Consumable consumable)
            throws SQLException {

        statement.setString(1, consumable.getType());
        statement.setString(2, consumable.getUnit());
        statement.setDouble(3, consumable.getStockLevel());
        statement.setDouble(4, consumable.getConsumptionRate());
        statement.setDouble(5, consumable.getReorderThreshold());
    }

    /**
     * Converts one database row into a Consumable object.
     */
    private Consumable mapConsumable(ResultSet results)
            throws SQLException {

        return new Consumable(
                results.getInt("consumable_id"),
                results.getString("type"),
                results.getString("unit"),
                results.getDouble("stock_level"),
                results.getDouble("consumption_rate"),
                results.getDouble("reorder_threshold")
        );
    }
}