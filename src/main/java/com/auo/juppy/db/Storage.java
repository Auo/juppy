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
import java.util.*;

public interface Storage {
    void saveResult(RunnerResult result);

    void createRunner(RunnerConfig config);

    void deleteRunner(UUID id);

    List<RunnerConfig> getAll();

    RunnerConfig get(UUID id);

    List<RunnerResult> getResult(UUID runnerId);

    class MemoryStorage implements Storage {

        Map<UUID, RunnerConfig> runners = new HashMap<>();
        Map<UUID, List<RunnerResult>> results = new HashMap<>();

        @Override
        public void saveResult(RunnerResult result) {
            results.computeIfAbsent(result.runnerId, res -> new ArrayList<>()).add(result);
            System.out.println(result);
        }

        @Override
        public void createRunner(RunnerConfig config) {

            if (runners.containsKey(config.id)) {
                throw new StorageException("Can't create a runner with ID: '" + config.id + "'. It already exists");
            }

            runners.put(config.id, config);
        }

        @Override
        public void deleteRunner(UUID id) {
            runners.remove(id);
        }

        @Override
        public List<RunnerConfig> getAll() {
            return new ArrayList<>(runners.values());
        }

        @Override
        public RunnerConfig get(UUID id) {
            return runners.get(id);
        }

        @Override
        public List<RunnerResult> getResult(UUID runnerId) {
            return results.get(runnerId);
        }
    }

    class SQLiteStorage implements Storage {
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
                String sql = "INSERT INTO result (statusCode, responseTime, runnerId, id) VALUES (?,?,?,?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, result.statusCode);
                    statement.setLong(2, result.responseTime);
                    statement.setString(3, result.runnerId.toString());
                    statement.setString(4, result.id.toString());

                    if (!statement.execute()) {
                        int updatedRows = statement.getUpdateCount();
                        System.out.println(updatedRows);
                    }
                }
            } catch (SQLException e) {
                throw new StorageException("Failed to insert result", e);
            }
        }

        @Override
        public void createRunner(RunnerConfig config) {
            try {
                String sql = "INSERT INTO runner (id, uri, timeout, interval) VALUES (?,?,?,?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, config.id.toString());
                    statement.setString(2, config.uri.toString());
                    statement.setLong(3, config.timeout);
                    statement.setLong(4, config.interval);

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
                String sql = "SELECT id, uri, timeout, interval  FROM runner";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    List<RunnerConfig> runners = new ArrayList<>();

                    if (statement.execute()) {
                        ResultSet resultSet = statement.getResultSet();
                        while (resultSet.next()) {
                            UUID id = UUID.fromString(resultSet.getString(1));
                            URI uri = URI.create(resultSet.getString(2));
                            long timeout = resultSet.getLong(3);
                            long interval = resultSet.getLong(4);

                            runners.add(RunnerConfig.create(uri, timeout, interval, id));
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
                String sql = "SELECT uri, timeout, interval  FROM runner WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, id.toString());

                    if (statement.execute()) {
                        ResultSet resultSet = statement.getResultSet();
                        resultSet.next();
                        URI uri = URI.create(resultSet.getString(1));
                        long timeout = resultSet.getLong(2);
                        long interval = resultSet.getLong(3);

                        return RunnerConfig.create(uri, timeout, interval, id);

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
                String sql = "SELECT id, statusCode, responseTime  FROM result WHERE runnerId = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, runnerId.toString());

                    List<RunnerResult> result = new ArrayList<>();

                    if (statement.execute()) {
                        ResultSet resultSet = statement.getResultSet();
                        while (resultSet.next()) {
                            UUID id = UUID.fromString(resultSet.getString(1));
                            int statusCode = resultSet.getInt(2);
                            long responseTime = resultSet.getLong(3);

                            result.add(new RunnerResult(statusCode, responseTime, runnerId, id));
                        }
                    }

                    return result;
                }
            } catch (SQLException e) {
                throw new StorageException("Failed to fetch runners", e);
            }
        }
    }
}
