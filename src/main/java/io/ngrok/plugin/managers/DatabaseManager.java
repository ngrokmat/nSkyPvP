package io.ngrok.plugin.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.ngrok.plugin.Main;
import io.ngrok.plugin.api.playerdata.PlayerData;
import io.ngrok.plugin.utils.logger.Logger;
import io.ngrok.plugin.utils.logger.types.Types;
import lombok.Getter;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    @Getter
    private static DatabaseManager manager = new DatabaseManager();
    private HikariDataSource dataSource;

    public void setupDatabase() {
        String host = Main.getConfiguration().getString("Configuration.Database.host");
        String port = Main.getConfiguration().getString("Configuration.Database.port");
        String database = Main.getConfiguration().getString("Configuration.Database.database");
        String username = Main.getConfiguration().getString("Configuration.Database.username");
        String password = Main.getConfiguration().getString("Configuration.Database.password");

        int maximumPoolSize = Main.getConfiguration().getInt("Configuration.Database.config.connectionPoolSize");
        int connectionPoolIdle = Main.getConfiguration().getInt("Configuration.Database.config.connectionPoolIdle");
        int connectionPoolTimeOut = Main.getConfiguration().getInt("Configuration.Database.connectionPoolTimeout");
        long connectionPoolLifetime = Main.getConfiguration().getInt("Configuration.Database.connectionPoolLifetime");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(connectionPoolIdle);
        config.setConnectionTimeout(connectionPoolTimeOut);
        config.setMaxLifetime(connectionPoolLifetime);

        dataSource = new HikariDataSource(config);

        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SHOW TABLES LIKE 'nskypvp'";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        createTable();
                    }
                }
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "An error occurred while checking the database, please check the database configuration. Error: DB-1");
        }
    }

    private void createTable() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS nskypvp (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "coins INT(5)," +
                    "kills INT(5)," +
                    "deaths INT(5)," +
                    "highest_streak INT(5)," +
                    "owned_kits VARCHAR(50)" +
                    ")";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "An error occurred while checking the database, please check the database configuration. Error: DB-2");
        }
    }

    public void closeConnectionPool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private String getStringValue(String columnName, UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT " + columnName + " FROM nskypvp WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(columnName);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "Error: DB-3");
        }
        return null;
    }

    private void updateStringValue(String columnName, UUID uuid, String value) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "UPDATE nskypvp SET " + columnName + " = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, value);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "Error: DB-4");
        }
    }

    private Integer getIntegerValue(String columnName, UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT " + columnName + " FROM nskypvp WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(columnName);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "Error: DB-5");
        }
        return null;
    }

    private void updateIntegerValue(String columnName, UUID uuid, int value) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "UPDATE nskypvp SET " + columnName + " = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, value);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "Error: DB-6");
        }
    }

    public boolean playerExist(UUID uuid) {

    }

    public void createPlayer(UUID uuid) {

    }

    public PlayerData getPlayerData(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM nskypvp WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int coins = resultSet.getInt("coins");
                        int kills = resultSet.getInt("kills");
                        int deaths = resultSet.getInt("deaths");
                        int highest_streak = resultSet.getInt("highest_streak");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "Error: DB-9");
        }
    }
}
