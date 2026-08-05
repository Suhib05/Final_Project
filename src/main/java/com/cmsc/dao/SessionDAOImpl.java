package com.cmsc.dao;

import com.cmsc.model.Session;
import com.cmsc.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of SessionDAO.
 * Handles equipment bookings, check-ins, and check-outs.
 *
 * @author Saleh Alhajj
 */
public class SessionDAOImpl implements SessionDAO {

    private static final String INSERT_SQL
            = "INSERT INTO sessions "
            + "(user_id, equipment_id, consumable_id, start_time, "
            + "end_time, materials_consumed) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL
            = "SELECT * FROM sessions WHERE session_id = ?";

    private static final String UPDATE_SQL
            = "UPDATE sessions SET user_id = ?, equipment_id = ?, "
            + "consumable_id = ?, start_time = ?, end_time = ?, "
            + "materials_consumed = ? WHERE session_id = ?";

    private static final String DELETE_SQL
            = "DELETE FROM sessions WHERE session_id = ?";

    private static final String FIND_ALL_SQL
            = "SELECT * FROM sessions ORDER BY session_id";

    private static final String FIND_ACTIVE_BY_EQUIPMENT_SQL
            = "SELECT * FROM sessions "
            + "WHERE equipment_id = ? AND end_time IS NULL "
            + "ORDER BY start_time DESC LIMIT 1";

    private static final String FIND_BY_USER_SQL
            = "SELECT * FROM sessions WHERE user_id = ? "
            + "ORDER BY start_time DESC";

    /**
     * Saves a new session.
     *
     * @param session session to save
     * @return saved session containing its generated identifier
     */
    @Override
    public Session save(Session session) {
        validateStartTime(session);

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setSessionParameters(statement, session);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    session.setSessionId(keys.getInt(1));
                }
            }

            return session;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save session", exception);
        }
    }

    /**
     * Finds a session using its database identifier.
     *
     * @param id session identifier
     * @return matching session, or null when it does not exist
     */
    @Override
    public Session findById(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapSession(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to find session", exception);
        }
    }

    /**
     * Updates an existing session.
     *
     * @param session session containing updated information
     * @return updated session
     */
    @Override
    public Session update(Session session) {
        validateStartTime(session);

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(UPDATE_SQL)) {

            setSessionParameters(statement, session);
            statement.setInt(7, session.getSessionId());
            statement.executeUpdate();

            return session;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update session", exception);
        }
    }

    /**
     * Deletes a session using its database identifier.
     *
     * @param id session identifier
     */
    @Override
    public void delete(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to delete session", exception);
        }
    }

    /**
     * Retrieves all sessions.
     *
     * @return all sessions ordered by identifier
     */
    @Override
    public List<Session> findAll() {
        List<Session> sessions = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_ALL_SQL);
                ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                sessions.add(mapSession(results));
            }

            return sessions;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to retrieve sessions", exception);
        }
    }

    /**
     * Creates a session for Member 1's booking code.
     *
     * @param session session to create
     * @return created session
     */
    @Override
    public Session createSession(Session session) {
        return save(session);
    }

    /**
     * Updates a session for Member 1's check-in/out code.
     *
     * @param session session to update
     * @return updated session
     */
    @Override
    public Session updateSession(Session session) {
        return update(session);
    }

    /**
     * Finds an active session for one piece of equipment.
     *
     * @param equipmentId equipment identifier
     * @return active session, or null when none exists
     */
    @Override
    public Session findActiveByEquipment(int equipmentId) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        FIND_ACTIVE_BY_EQUIPMENT_SQL)) {

            statement.setInt(1, equipmentId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapSession(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find active equipment session", exception
            );
        }
    }

    /**
     * Finds all sessions belonging to one user.
     *
     * @param userId user identifier
     * @return sessions belonging to the user
     */
    @Override
    public List<Session> findByUserId(int userId) {
        List<Session> sessions = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_USER_SQL)) {

            statement.setInt(1, userId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    sessions.add(mapSession(results));
                }
            }

            return sessions;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find sessions by user", exception
            );
        }
    }

    /**
     * Places session values into an INSERT or UPDATE statement.
     */
    private void setSessionParameters(
            PreparedStatement statement, Session session)
            throws SQLException {

        statement.setInt(1, session.getUserId());
        statement.setInt(2, session.getEquipmentId());

        if (session.getConsumableId() == null) {
            statement.setNull(3, Types.INTEGER);
        } else {
            statement.setInt(3, session.getConsumableId());
        }

        statement.setTimestamp(
                4, Timestamp.valueOf(session.getStartTime())
        );

        if (session.getEndTime() == null) {
            statement.setNull(5, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(
                    5, Timestamp.valueOf(session.getEndTime())
            );
        }

        statement.setDouble(6, session.getMaterialsConsumed());
    }

    /**
     * Converts one session database row into a Session object.
     */
    private Session mapSession(ResultSet results) throws SQLException {
        int storedConsumableId = results.getInt("consumable_id");
        Integer consumableId = results.wasNull()
                ? null : storedConsumableId;

        Timestamp startTimestamp = results.getTimestamp("start_time");
        Timestamp endTimestamp = results.getTimestamp("end_time");

        return new Session(
                results.getInt("session_id"),
                results.getInt("user_id"),
                results.getInt("equipment_id"),
                consumableId,
                startTimestamp == null
                        ? null : startTimestamp.toLocalDateTime(),
                endTimestamp == null
                        ? null : endTimestamp.toLocalDateTime(),
                results.getDouble("materials_consumed")
        );
    }

    /**
     * Confirms that the required session start time was supplied.
     */
    private void validateStartTime(Session session) {
        if (session.getStartTime() == null) {
            throw new IllegalArgumentException(
                    "Session start time is required"
            );
        }
    }
}