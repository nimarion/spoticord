package de.biosphere.spoticord.database.impl.mysql;

import com.zaxxer.hikari.HikariDataSource;
import de.biosphere.spoticord.database.dao.TrackDao;
import de.biosphere.spoticord.database.model.SpotifyTrack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TrackImplMySql implements TrackDao {

    private final HikariDataSource hikariDataSource;

    public TrackImplMySql(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public Integer getTrackAmount() {
        try (final Connection connection = hikariDataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT COUNT(*) AS Count FROM Tracks")) {
                final ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("Count");
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public Integer getListensAmount(String guildId) {
        return getListensAmount(guildId, null);
    }

    @Override
    public Integer getListensAmount() {
        try (final Connection connection = hikariDataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT COUNT(*) AS Count FROM `Listens`")) {
                final ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("Count");
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public Integer getListensAmount(String guildId, String userId) {
        try (final Connection connection = hikariDataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection
                    .prepareStatement(userId == null ? "SELECT COUNT(*) AS Count FROM `Listens` WHERE GuildId=?"
                            : "SELECT COUNT(*) AS Count FROM `Listens` WHERE GuildId=? AND UserId=?")) {
                preparedStatement.setString(1, guildId);
                if (userId != null) {
                    preparedStatement.setString(2, userId);
                }
                final ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("Count");
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public void insertTrack(SpotifyTrack spotifyTrack, String userId, String guildId) {
        try (final Connection connection = hikariDataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT IGNORE INTO `Tracks` (`Id`, `Artists`, `AlbumImageUrl`, `AlbumTitle`, `TrackTitle`, `Duration`) VALUES (?, ?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, spotifyTrack.id());
                preparedStatement.setString(2, spotifyTrack.artists());
                preparedStatement.setString(3, spotifyTrack.albumImageUrl());
                preparedStatement.setString(4, spotifyTrack.albumTitle());
                preparedStatement.setString(5, spotifyTrack.trackTitle());
                preparedStatement.setLong(6, spotifyTrack.duration());
                preparedStatement.execute();
            }

            try (final PreparedStatement preparedStatement2 = connection.prepareStatement(
                    "INSERT INTO `Listens` (`Id`, `Timestamp`, `TrackId`, `GuildId`, `UserId`) VALUES (NULL, CURRENT_TIMESTAMP, ?, ?, ?)")) {
                preparedStatement2.setString(1, spotifyTrack.id());
                preparedStatement2.setString(2, guildId);
                preparedStatement2.setString(3, userId);
                preparedStatement2.execute();
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Map<SpotifyTrack, Integer> getTopTracks(String guildId, String userId, Integer count, Integer lastDays) {
        final Map<SpotifyTrack, Integer> topMap = new LinkedHashMap<>();
        try (final Connection connection = hikariDataSource.getConnection()) {
            final String lastDaysQuery = lastDays == 0 ? ""
                    : "AND Listens.Timestamp >= DATE(NOW()) - INTERVAL " + lastDays + " DAY ";
            try (final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT Tracks.*, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? "
                            + lastDaysQuery + "GROUP BY Listens.`TrackId` ORDER BY COUNT(*) DESC LIMIT ?"
                    : "SELECT Tracks.*, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? AND Listens.UserId=? "
                            + lastDaysQuery + "GROUP BY Listens.`TrackId` ORDER BY COUNT(*) DESC LIMIT ?")) {
                preparedStatement.setString(1, guildId);
                if (userId != null) {
                    preparedStatement.setString(2, userId);
                    preparedStatement.setInt(3, count);
                } else {
                    preparedStatement.setInt(2, count);
                }

                final ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    final SpotifyTrack spotifyTrack = getTrackFromResultSet(resultSet);
                    final Integer listener = resultSet.getInt("Listener");
                    topMap.put(spotifyTrack, listener);
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return topMap;
    }

    private SpotifyTrack getTrackFromResultSet(final ResultSet resultSet) throws SQLException {
        return new SpotifyTrack(resultSet.getString("Id"), resultSet.getString("Artists"),
                resultSet.getString("AlbumTitle"), resultSet.getString("TrackTitle"),
                resultSet.getString("AlbumImageUrl"), resultSet.getLong("Duration"));
    }

    @Override
    public List<SpotifyTrack> getLastTracks(String guildId) {
        return getLastTracks(guildId, null);
    }

    @Override
    public List<SpotifyTrack> getLastTracks(String guildId, String userId) {
        final List<SpotifyTrack> tracks = new LinkedList<>();
        try (final Connection connection = hikariDataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT Tracks.* FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE GuildId=? ORDER BY `Listens`.`Timestamp`  DESC LIMIT 10"
                    : "SELECT Tracks.* FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE GuildId=? AND UserId=? ORDER BY `Listens`.`Timestamp`  DESC LIMIT 10")) {
                preparedStatement.setString(1, guildId);
                if (userId != null) {
                    preparedStatement.setString(2, userId);
                }
                final ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    final SpotifyTrack spotifyTrack = getTrackFromResultSet(resultSet);
                    tracks.add(spotifyTrack);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tracks;
    }

}