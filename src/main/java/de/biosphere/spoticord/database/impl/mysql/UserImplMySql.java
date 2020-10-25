package de.biosphere.spoticord.database.impl.mysql;

import com.zaxxer.hikari.HikariDataSource;
import de.biosphere.spoticord.database.dao.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserImplMySql implements UserDao {

    private final HikariDataSource hikariDataSource;

    public UserImplMySql(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public Long getListenTime(String guildId) {
        return getListenTime(guildId, null);
    }

    @Override
    public Long getListenTime(String guildId, String userId, Integer lastDays) {
        try (final Connection connection = hikariDataSource.getConnection()) {
            final String lastDaysQuery = lastDays == 0 ? ""
                    : " AND Listens.Timestamp >= DATE(NOW()) - INTERVAL " + lastDays + " DAY";
            final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT SUM(Tracks.Duration) AS Duration FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=?"
                            + lastDaysQuery
                    : "SELECT SUM(Tracks.Duration) AS Duration FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? AND Listens.UserId=?"
                            + lastDaysQuery);
            preparedStatement.setString(1, guildId);
            if (userId != null) {
                preparedStatement.setString(2, userId);
            }
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("Duration");
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }

    @Override
    public Map<String, Integer> getTopUsers(String guildId, Integer count, Integer lastDays) {
        final Map<String, Integer> topMap = new LinkedHashMap<>();
        try (final Connection connection = hikariDataSource.getConnection()) {
            final String lastDaysQuery = lastDays == 0 ? ""
                    : "AND Listens.Timestamp >= DATE(NOW()) - INTERVAL " + lastDays + " DAY ";

            final PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT UserId, COUNT(*) AS Listener FROM `Listens` WHERE GuildId=? "
                            + lastDaysQuery + "GROUP BY `UserId` ORDER BY COUNT(*) DESC LIMIT ?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setInt(2, count);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                topMap.put(resultSet.getString("UserId"), resultSet.getInt("Listener"));
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return topMap;
    }

    @Override
    public Map<String, Long> getTopListenersByTime(String guildId, Integer count, final Integer lastDays) {
        final Map<String, Long> topMap = new LinkedHashMap<>();
        try (final Connection connection = hikariDataSource.getConnection()) {
            final String lastDaysQuery = lastDays == 0 ? ""
                    : "AND Listens.Timestamp >= DATE(NOW()) - INTERVAL " + lastDays + " DAY ";
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT SUM(Tracks.Duration) AS Duration, Listens.UserId  FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? "
                            + lastDaysQuery + " GROUP BY Listens.UserId ORDER BY Duration DESC LIMIT ?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setInt(2, count);

            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                topMap.put(resultSet.getString("UserId"), resultSet.getLong("Duration"));
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return topMap;
    }

    @Override
    public void deleteUser(String guildId, String userId) {
        try (final Connection connection = hikariDataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM Listens WHERE GuildId=? AND UserId=?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, userId);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public Long getMostListensTime(String guildId) {
        return getMostListensTime(guildId, null);
    }

    @Override
    public Long getMostListensTime(String guildId, String userId) {
        try (final Connection connection = hikariDataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT SEC_TO_TIME(AVG(TIME_TO_SEC(cast(Timestamp as Time)))) AS result FROM Listens WHERE GuildId=?"
                    : "SELECT SEC_TO_TIME(AVG(TIME_TO_SEC(cast(Timestamp as Time)))) AS result FROM Listens WHERE GuildId=? AND UserId=?");
            preparedStatement.setString(1, guildId);
            if (userId != null) {
                preparedStatement.setString(2, userId);
            }
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getString("result") != null) {
                return resultSet.getTime("result").getTime();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }

}