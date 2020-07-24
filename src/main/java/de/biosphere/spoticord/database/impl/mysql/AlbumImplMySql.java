package de.biosphere.spoticord.database.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zaxxer.hikari.HikariDataSource;

import de.biosphere.spoticord.database.dao.AlbumDao;

public class AlbumImplMySql implements AlbumDao {

    private final HikariDataSource hikariDataSource;

    public AlbumImplMySql(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public Map<String, Integer> getTopAlbum(String guildId, String userId, Integer count) {
        final Map<String, Integer> topMap = new LinkedHashMap<>();
        try (final Connection connection = hikariDataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT Tracks.AlbumTitle, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? GROUP BY `AlbumTitle` ORDER BY COUNT(*) DESC LIMIT ?"
                    : "SELECT Tracks.AlbumTitle, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? AND Listens.UserId=? GROUP BY `AlbumTitle` ORDER BY COUNT(*) DESC LIMIT ?");
            preparedStatement.setString(1, guildId);
            if (userId != null) {
                preparedStatement.setString(2, userId);
                preparedStatement.setInt(3, count);
            } else {
                preparedStatement.setInt(2, count);
            }

            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                topMap.put(resultSet.getString("AlbumTitle"), resultSet.getInt("Listener"));
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return topMap;
    }

    @Override
    public Map<String, Integer> getTopAlbum(String guildId, Integer count) {
        return getTopAlbum(guildId, null, count);
    }

}