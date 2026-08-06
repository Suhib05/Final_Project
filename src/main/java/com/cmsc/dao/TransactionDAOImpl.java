package com.cmsc.dao;

import com.cmsc.model.Transaction;
import com.cmsc.model.TransactionSource;
import com.cmsc.model.TransactionType;
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
 * MySQL implementation of TransactionDAO.
 * Reads and writes the credit/debit ledger.
 *
 * @author Annour Mohamed
 */
public class TransactionDAOImpl implements TransactionDAO {

    private static final String INSERT_SQL
            = "INSERT INTO transactions "
            + "(user_id, type, amount, source, reference_id, transaction_date) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_SQL
            = "SELECT * FROM transactions WHERE transaction_id = ?";

    private static final String UPDATE_SQL
            = "UPDATE transactions SET user_id = ?, type = ?, amount = ?, "
            + "source = ?, reference_id = ?, transaction_date = ? "
            + "WHERE transaction_id = ?";

    private static final String DELETE_SQL
            = "DELETE FROM transactions WHERE transaction_id = ?";

    private static final String FIND_ALL_SQL
            = "SELECT * FROM transactions ORDER BY transaction_date DESC";

    private static final String FIND_BY_USER_SQL
            = "SELECT * FROM transactions WHERE user_id = ? "
            + "ORDER BY transaction_date DESC";

    private static final String FIND_BY_USER_BETWEEN_SQL
            = "SELECT * FROM transactions WHERE user_id = ? "
            + "AND transaction_date >= ? AND transaction_date < ? "
            + "ORDER BY transaction_date";

    private static final String SUM_BY_TYPE_SQL
            = "SELECT COALESCE(SUM(amount), 0) FROM transactions "
            + "WHERE user_id = ? AND type = ?";

    /**
     * Saves a new ledger entry. When no date has been set the current time is
     * stamped on it, so an entry is never written without one.
     *
     * @param transaction entry to save
     * @return saved entry containing its generated identifier
     */
    @Override
    public Transaction save(Transaction transaction) {
        validate(transaction);

        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(statement, transaction);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setTransactionId(keys.getInt(1));
                }
            }

            return transaction;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save transaction", exception);
        }
    }

    /**
     * Finds a ledger entry using its database identifier.
     *
     * @param id transaction identifier
     * @return matching entry, or null when it does not exist
     */
    @Override
    public Transaction findById(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapTransaction(results);
                }
            }

            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to find transaction", exception);
        }
    }

    /**
     * Updates an existing ledger entry. Corrections are rare, but the ledger
     * must still support them for a mistaken entry to be put right.
     *
     * @param transaction entry containing updated information
     * @return updated entry
     */
    @Override
    public Transaction update(Transaction transaction) {
        validate(transaction);

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(UPDATE_SQL)) {

            setParameters(statement, transaction);
            statement.setInt(7, transaction.getTransactionId());
            statement.executeUpdate();

            return transaction;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update transaction",
                    exception);
        }
    }

    /**
     * Deletes a ledger entry.
     *
     * @param id transaction identifier
     */
    @Override
    public void delete(int id) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to delete transaction",
                    exception);
        }
    }

    /**
     * Returns every ledger entry in the system, newest first.
     *
     * @return all ledger entries
     */
    @Override
    public List<Transaction> findAll() {
        return queryList(FIND_ALL_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByUserId(int userId) {
        return queryList(FIND_BY_USER_SQL, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByUserBetween(int userId, LocalDateTime from,
            LocalDateTime to) {

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Both ends of the period are required");
        }

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(FIND_BY_USER_BETWEEN_SQL)) {

            statement.setInt(1, userId);
            statement.setTimestamp(2, Timestamp.valueOf(from));
            statement.setTimestamp(3, Timestamp.valueOf(to));

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    transactions.add(mapTransaction(results));
                }
            }

            return transactions;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to find transactions for the period", exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sumByType(int userId, TransactionType type) {
        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(SUM_BY_TYPE_SQL)) {

            statement.setInt(1, userId);
            statement.setString(2, type.name());

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getDouble(1);
                }
            }

            return 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to total transactions",
                    exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateBalance(int userId) {
        return sumByType(userId, TransactionType.CREDIT)
                - sumByType(userId, TransactionType.DEBIT);
    }

    /**
     * Runs a query that returns a list of ledger entries.
     *
     * @param sql statement to run
     * @param userId optional user identifier bound to the first parameter
     * @return matching entries
     */
    private List<Transaction> queryList(String sql, int... userId) {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {

            if (userId.length > 0) {
                statement.setInt(1, userId[0]);
            }

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    transactions.add(mapTransaction(results));
                }
            }

            return transactions;
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read transactions",
                    exception);
        }
    }

    /**
     * Binds the six writable columns of a ledger entry, in schema order.
     *
     * @param statement statement to bind
     * @param transaction entry supplying the values
     * @throws SQLException when a value cannot be bound
     */
    private void setParameters(PreparedStatement statement,
            Transaction transaction) throws SQLException {

        statement.setInt(1, transaction.getUserId());
        statement.setString(2, transaction.getType().name());
        statement.setDouble(3, transaction.getAmount());
        statement.setString(4, transaction.getSource().name());

        if (transaction.getReferenceId() == null) {
            statement.setNull(5, Types.INTEGER);
        } else {
            statement.setInt(5, transaction.getReferenceId());
        }

        statement.setTimestamp(6,
                Timestamp.valueOf(transaction.getTransactionDate()));
    }

    /**
     * Rebuilds a ledger entry from the current row of a result set.
     *
     * @param results result set positioned on a row
     * @return the entry that row represents
     * @throws SQLException when a column cannot be read
     */
    private Transaction mapTransaction(ResultSet results) throws SQLException {
        Integer referenceId = results.getInt("reference_id");
        if (results.wasNull()) {
            referenceId = null;
        }

        Timestamp date = results.getTimestamp("transaction_date");

        return new Transaction(
                results.getInt("transaction_id"),
                results.getInt("user_id"),
                TransactionType.valueOf(results.getString("type")),
                results.getDouble("amount"),
                TransactionSource.valueOf(results.getString("source")),
                referenceId,
                date == null ? null : date.toLocalDateTime()
        );
    }

    /**
     * Rejects entries that would corrupt the ledger, so a bad row never
     * reaches the database in the first place.
     *
     * @param transaction entry to check
     */
    private void validate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction must not be null");
        }
        if (transaction.getType() == null) {
            throw new IllegalArgumentException(
                    "Transaction type is required");
        }
        if (transaction.getSource() == null) {
            throw new IllegalArgumentException(
                    "Transaction source is required");
        }
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be positive; direction is "
                    + "carried by the type, not the sign");
        }
    }
}
