package com.auo.juppy.db;

import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerConfig;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLiteStorage implements Storage {
    private final Connection connection;

    public SQLiteStorage(String dbPath) {
        Flyway flyway;
        try {
            flyway = Flyway.configure().configuration(Map.of("flyway.url", "jdbc:sqlite:" + dbPath)).load();
            flyway.migrate();
        } catch (FlywayException e) {
            throw new StorageException("Failed to setup flyway DB", e);
        }
        try {
            this.connection = flyway.getConfiguration().getDataSource().getConnection();
        } catch (SQLException e) {
            throw new StorageException("Failed to open connection to DB", e);
        }
    }

    @Override
    public void saveResult(RunnerResult result) {
        try {
            String sql = "INSERT INTO result (statusCode, responseTime, runnerId, id, timestamp) VALUES (?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, result.statusCode);
                statement.setLong(2, result.responseTime);
                statement.setString(3, result.runnerId.toString());
                statement.setString(4, result.id.toString());
                statement.setLong(5, result.timestamp);

                if (!statement.execute()) {
                    int updatedRows = statement.getUpdateCount();

                    if (updatedRows != 1) {
                        throw new StorageException("Expected one inserted row. Got " + updatedRows);
                    }
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to insert result", e);
        }
    }

    @Override
    public void createRunner(RunnerConfig config) {
        try {
            String sql = "INSERT INTO runner (id, uri, timeout, interval, created) VALUES (?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, config.id.toString());
                statement.setString(2, config.uri.toString());
                statement.setLong(3, config.timeout);
                statement.setLong(4, config.interval);
                statement.setLong(5, config.created);

                if (!statement.execute()) {
                    int updatedRows = statement.getUpdateCount();
                    System.out.println(updatedRows);
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to create runner", e);
        }
    }

    @Override
    public void deleteRunner(UUID id) {
        try {
            String sql = "DELETE FROM runner WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id.toString());

                if (!statement.execute()) {
                    int updatedRows = statement.getUpdateCount();
                    System.out.println(updatedRows);
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to remove runner", e);
        }
    }

    @Override
    public List<RunnerConfig> getAll() {
        try {
            String sql = "SELECT id, uri, timeout, interval, created  FROM runner";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                List<RunnerConfig> runners = new ArrayList<>();

                if (statement.execute()) {
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        UUID id = UUID.fromString(resultSet.getString(1));
                        URI uri = URI.create(resultSet.getString(2));
                        long timeout = resultSet.getLong(3);
                        long interval = resultSet.getLong(4);
                        long created = resultSet.getLong(5);

                        runners.add(RunnerConfig.create(uri, timeout, interval, id, created));
                    }
                }

                return runners;
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to fetch runners", e);
        }
    }

    @Override
    public RunnerConfig get(UUID id) {
        try {
            String sql = "SELECT uri, timeout, interval, created  FROM runner WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id.toString());

                if (statement.execute()) {
                    ResultSet resultSet = statement.getResultSet();
                    resultSet.next();
                    URI uri = URI.create(resultSet.getString(1));
                    long timeout = resultSet.getLong(2);
                    long interval = resultSet.getLong(3);
                    long created = resultSet.getLong(4);

                    return RunnerConfig.create(uri, timeout, interval, id, created);

                }

                return null;
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to fetch runners", e);
        }
    }

    @Override
    public List<RunnerResult> getResult(UUID runnerId) {
        try {
            String sql = "SELECT id, statusCode, responseTime, timestamp  FROM result WHERE runnerId = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, runnerId.toString());

                List<RunnerResult> result = new ArrayList<>();

                if (statement.execute()) {
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        UUID id = UUID.fromString(resultSet.getString(1));
                        int statusCode = resultSet.getInt(2);
                        long responseTime = resultSet.getLong(3);
                        long timestamp = resultSet.getLong(4);

                        result.add(new RunnerResult(statusCode, responseTime, runnerId, id, timestamp));
                    }
                }

                return result;
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to fetch runners", e);
        }
    }
}
