package com.cmsc.dao;

import com.cmsc.model.Training;
import com.cmsc.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of TrainingDAO.
 * Records the intro and safety sessions Trainers deliver.
 *
 * @author Annour Mohamed
 */
public class TrainingDAOImpl implements TrainingDAO {

    private static final String INSERT_SQL
            = "INSERT INTO trainings (trainer_id, equipment_id, "
            + "date_delivered) VALUES (?, ?, ?)";

    private static final String FIND_BY_ID_SQL
            = "SELECT * FROM trainings WHERE training_id = ?";

    private static final String UPDATE_SQL
            = "UPDATE trainings SET trainer_id = ?, equipment_id = ?, "
            + "date_delivered = ? WHERE training_id = ?";

    private static final String DELETE_SQL
            = "DELETE FROM trainings WHERE training_id = ?";

    private static final String FIND_ALL_SQL
            = "SELECT * FROM trainings ORDER BY date_delivered DESC";

    private static final String FIND_BY_TRAINER_SQL
            = "SELECT * FROM trainings WHERE trainer_id = ? "
            + "ORDER BY date_delivered DESC";

    private static final String FIND_BY_EQUIPMENT_SQL
            = "SELECT * FROM trainings WHERE equipment_id = ? "
            + "ORDER BY date_delivered DESC";

    private static final String COUNT_BY_TRAINER_SQL
            = "SELECT COUNT(*) FROM trainings WHERE trainer_id = ?";

    /**
     * Saves a delivered training session.
     *
     * @param training session to save
     * @return saved session containing its generated identifier
     */
    @Override
    public Training save(Training training) {
        validate(training);

        if (training.getDateDelivered() == null) {
            training.setDateDelivered(LocalDateTime.now());
        }

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(statement, training);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    training.setTrainingId(keys.getInt(1));
                }
            }

            return training;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save training", exception);
        }
    }

    /**
     * Finds a training session using its database identifier.
     *
     * @param id training identifier
     * @return matching session, or null when it does not exist
     */
    @Override
    public Training findById(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapTraining(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to find training", exception);
        }
    }

    /**
     * Updates an existing training session.
     *
     * @param training session containing updated information
     * @return updated session
     */
    @Override
    public Training update(Training training) {
        validate(training);

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(UPDATE_SQL)) {

            setParameters(statement, training);
            statement.setInt(4, training.getTrainingId());
            statement.executeUpdate();

            return training;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update training", exception);
        }
    }

    /**
     * Deletes a training session.
     *
     * @param id training identifier
     */
    @Override
    public void delete(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to delete training", exception);
        }
    }

    /**
     * Returns every training session on record, newest first.
     *
     * @return all delivered sessions
     */
    @Override
    public List<Training> findAll() {
        return queryList(FIND_ALL_SQL, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Training> findByTrainerId(int trainerId) {
        return queryList(FIND_BY_TRAINER_SQL, trainerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Training> findByEquipmentId(int equipmentId) {
        return queryList(FIND_BY_EQUIPMENT_SQL, equipmentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByTrainerId(int trainerId) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(COUNT_BY_TRAINER_SQL)) {

            statement.setInt(1, trainerId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getInt(1);
                }
            }

            return 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to count trainings", exception);
        }
    }

    /**
     * Runs a query returning a list of training sessions.
     *
     * @param sql statement to run
     * @param parameter value bound to the first placeholder, or null when the
     *        statement takes none
     * @return matching sessions
     */
    private List<Training> queryList(String sql, Integer parameter) {
        List<Training> trainings = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {

            if (parameter != null) {
                statement.setInt(1, parameter);
            }

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    trainings.add(mapTraining(results));
                }
            }

            return trainings;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read trainings", exception);
        }
    }

    /**
     * Binds the three writable columns of a training session.
     *
     * @param statement statement to bind
     * @param training session supplying the values
     * @throws SQLException when a value cannot be bound
     */
    private void setParameters(PreparedStatement statement, Training training)
            throws SQLException {

        statement.setInt(1, training.getTrainerId());
        statement.setInt(2, training.getEquipmentId());
        statement.setTimestamp(3,
                Timestamp.valueOf(training.getDateDelivered()));
    }

    /**
     * Rebuilds a training session from the current row of a result set.
     *
     * @param results result set positioned on a row
     * @return the session that row represents
     * @throws SQLException when a column cannot be read
     */
    private Training mapTraining(ResultSet results) throws SQLException {
        Timestamp delivered = results.getTimestamp("date_delivered");

        return new Training(
                results.getInt("training_id"),
                results.getInt("trainer_id"),
                results.getInt("equipment_id"),
                delivered == null ? null : delivered.toLocalDateTime()
        );
    }

    /**
     * Rejects sessions that could not be stored correctly.
     *
     * @param training session to check
     */
    private void validate(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training must not be null");
        }
        if (training.getTrainerId() <= 0) {
            throw new IllegalArgumentException("A trainer is required");
        }
        if (training.getEquipmentId() <= 0) {
            throw new IllegalArgumentException("Equipment is required");
        }
    }
}
