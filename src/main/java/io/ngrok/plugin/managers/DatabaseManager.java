package io.ngrok.plugin.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.ngrok.plugin.Main;
import io.ngrok.plugin.api.playerdata.PlayerData;
import io.ngrok.plugin.utils.logger.Logger;
import io.ngrok.plugin.utils.logger.types.Types;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    @Getter
    private static DatabaseManager manager = new DatabaseManager();
    private HikariDataSource dataSource;

    public void setupDatabase() {
        String host = Main.getConfiguration().getString("configuration.mysql.host");
        String port = Main.getConfiguration().getString("configuration.mysql.port");
        String database = Main.getConfiguration().getString("configuration.mysql.database");
        String username = Main.getConfiguration().getString("configuration.mysql.username");
        String password = Main.getConfiguration().getString("configuration.mysql.password");

        int maximumPoolSize = Main.getConfiguration().getInt("configuration.mysql.config.connectionPoolSize");
        int connectionPoolIdle = Main.getConfiguration().getInt("configuration.mysql.config.connectionPoolIdle");
        int connectionPoolTimeOut = Main.getConfiguration().getInt("configuration.mysql.connectionPoolTimeout");
        long connectionPoolLifetime = Main.getConfiguration().getInt("configuration.mysql.connectionPoolLifetime");

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
            try {
                String sql2 = "SHOW TABLES LIKE 'nskypvp_playerskits'";
                try (PreparedStatement statement = connection.prepareStatement(sql2)) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            createKitsTable();
                        }
                    }
                }
            } catch (SQLException e) {
                Logger.log(Types.ERROR, "An error occurred while checking the database, please check the database configuration. Error: DB-1");
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
                    ")";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "An error occurred while checking the database, please check the database configuration. Error: DB-2");
        }
    }

    private void createKitsTable() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS nskypvp_playerskits (" +
                    "uuid VARCHAR(36)," +
                    "kit_id INT," +
                    "PRIMARY KEY (uuid, kit_id)" +
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
        String sql = "SELECT COUNT(*) FROM nskypvp WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "An error occurred while checking if the player exists. Error: " + e.getMessage());
        }

        return false;
    }

    public void createPlayer(UUID uuid) {
        String sqlInsertPlayer = "INSERT INTO nskypvp (uuid, coins, kills, deaths, highest_streak) VALUES (?, 0, 0, 0, 0)";
        String sqlInsertKit = "INSERT INTO nskypvp_playerskits (uuid, kit_id) VALUES (?, 1)";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement playerStatement = connection.prepareStatement(sqlInsertPlayer)) {
                playerStatement.setString(1, uuid.toString());
                playerStatement.executeUpdate();
            }
            try (PreparedStatement kitStatement = connection.prepareStatement(sqlInsertKit)) {
                kitStatement.setString(1, uuid.toString());
                kitStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "An error occurred while creating the player. Error: " + e.getMessage());
            try {
                dataSource.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                Logger.log(Types.ERROR, "An error occurred while rolling back the transaction. Error: " + rollbackEx.getMessage());
            }
        }
    }



    public PlayerData getPlayerData(UUID uuid) {
        String sql = "SELECT nskypvp.coins, nskypvp.kills, nskypvp.deaths, nskypvp.highest_streak, " +
                "nk.kit_id FROM nskypvp " +
                "LEFT JOIN nskypvp_playerskits nk ON nskypvp.uuid = nk.uuid WHERE nskypvp.uuid = ?";

        List<Integer> ownedKits = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int coins = resultSet.getInt("coins");
                    int kills = resultSet.getInt("kills");
                    int deaths = resultSet.getInt("deaths");
                    int highestStreak = resultSet.getInt("highest_streak");
                    do {
                        int kitId = resultSet.getInt("kit_id");
                        ownedKits.add(kitId);
                    } while (resultSet.next());

                    return new PlayerData(uuid, coins, kills, deaths, 0, highestStreak, ownedKits);
                }
            }
        } catch (SQLException e) {
            Logger.log(Types.ERROR, "Error retrieving player data for UUID " + uuid + ": " + e.getMessage());
        }
        return null;
    }
}
