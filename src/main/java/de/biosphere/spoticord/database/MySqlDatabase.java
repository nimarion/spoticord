package de.biosphere.spoticord.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import de.biosphere.spoticord.database.model.SpotifyTrack;

public class MySqlDatabase implements Database {

    private final HikariDataSource dataSource;

    public MySqlDatabase(final String host, final String username, final String password, final String database,
            final int port) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        dataSource = new HikariDataSource(config);

        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Listens` ( `Id` INT NOT NULL AUTO_INCREMENT , `Timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , `TrackId` VARCHAR(22) NOT NULL , `GuildId` VARCHAR(100) NOT NULL , `UserId` VARCHAR(100) NOT NULL , INDEX `Id` (`Id`));");
            preparedStatement.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Tracks` ( `Id` VARCHAR(22) NOT NULL , `Artists` VARCHAR(200) NOT NULL , `AlbumImageUrl` VARCHAR(2083) NOT NULL , `AlbumTitle` VARCHAR(200) NOT NULL , `TrackTitle` VARCHAR(200) NOT NULL , `Duration` BIGINT UNSIGNED NOT NULL , PRIMARY KEY (`Id`));");
            preparedStatement.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void insertTrackData(final SpotifyTrack spotifyTrack, final String userId, final String guildId) {
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT IGNORE INTO `Tracks` (`Id`, `Artists`, `AlbumImageUrl`, `AlbumTitle`, `TrackTitle`, `Duration`) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, spotifyTrack.id());
            preparedStatement.setString(2, spotifyTrack.artists());
            preparedStatement.setString(3, spotifyTrack.albumImageUrl());
            preparedStatement.setString(4, spotifyTrack.albumTitle());
            preparedStatement.setString(5, spotifyTrack.trackTitle());
            preparedStatement.setLong(6, spotifyTrack.duration());
            preparedStatement.execute();

            final PreparedStatement preparedStatement2 = connection.prepareStatement(
                    "INSERT INTO `Listens` (`Id`, `Timestamp`, `TrackId`, `GuildId`, `UserId`) VALUES (NULL, CURRENT_TIMESTAMP, ?, ?, ?)");
            preparedStatement2.setString(1, spotifyTrack.id());
            preparedStatement2.setString(2, guildId);
            preparedStatement2.setString(3, userId);
            preparedStatement2.execute();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public SpotifyTrack getRandomTrack(final String guildId) {
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT Tracks.* FROM Listens INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE GuildId=? ORDER BY RAND() LIMIT 1");
            preparedStatement.setString(1, guildId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final SpotifyTrack spotifyTrack = getTrackFromResultSet(resultSet);
                return spotifyTrack;
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Integer> getTopListeners(final String guildId, final Integer count) {
        final Map<String, Integer> topMap = new LinkedHashMap<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT UserId, COUNT(*) AS Listener FROM `Listens` WHERE GuildId=? GROUP BY `UserId` ORDER BY COUNT(*) DESC LIMIT ?");
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
    public Map<SpotifyTrack, Integer> getTopTracks(final String guildId, final String userId, final Integer count) {
        final Map<SpotifyTrack, Integer> topMap = new LinkedHashMap<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT Tracks.*, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? GROUP BY `TrackId` ORDER BY COUNT(*) DESC LIMIT ?"
                    : "SELECT Tracks.*, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE GuildId=? AND UserId=? GROUP BY `TrackId` ORDER BY COUNT(*) DESC LIMIT ?");
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
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return topMap;
    }

    @Override
    public Integer getTrackAmount() {
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT COUNT(*) AS Count FROM Tracks");
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("Count");
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public Integer getListensAmount(final String guildId) {
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                    .prepareStatement(guildId == null ? "SELECT COUNT(*) AS Count FROM `Listens`"
                            : "SELECT COUNT(*) AS Count FROM `Listens` WHERE GuildId=?");
            if (guildId != null) {
                preparedStatement.setString(1, guildId);
            }
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("Count");
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public Long getListenTime(final String guildId, final String userId) {
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT SUM(Tracks.Duration) AS Duration FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=?"
                    : "SELECT SUM(Tracks.Duration) AS Duration FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? AND Listens.UserId=?");
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
    public Map<String, Long> getTopListenersByTime(final String guildId, final Integer count) {
        final Map<String, Long> topMap = new LinkedHashMap<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT SUM(Tracks.Duration) AS Duration, Listens.UserId  FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? GROUP BY Listens.UserId ORDER BY Duration DESC LIMIT ?");
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
    public Map<String, Integer> getTopArtists(final String guildId, final String userId, final Integer count) {
        final Map<String, Integer> topMap = new LinkedHashMap<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(userId == null
                    ? "SELECT Tracks.Artists, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? GROUP BY `Artists` ORDER BY COUNT(*) DESC LIMIT ?"
                    : "SELECT Tracks.Artists, COUNT(*) AS Listener FROM `Listens` INNER JOIN Tracks ON Listens.TrackId=Tracks.Id WHERE Listens.GuildId=? AND Listens.UserId=? GROUP BY `Artists` ORDER BY COUNT(*) DESC LIMIT ?");
            preparedStatement.setString(1, guildId);
            if (userId != null) {
                preparedStatement.setString(2, userId);
                preparedStatement.setInt(3, count);
            } else {
                preparedStatement.setInt(2, count);
            }

            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                topMap.put(resultSet.getString("Artists"), resultSet.getInt("Listener"));
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return topMap;
    }

    @Override
    public Map<String, Integer> getTopAlbum(final String guildId, final String userId, final Integer count) {
        final Map<String, Integer> topMap = new LinkedHashMap<>();
        try (final Connection connection = getConnection()) {
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

    private final Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private SpotifyTrack getTrackFromResultSet(final ResultSet resultSet) throws SQLException {
        final SpotifyTrack spotifyTrack = new SpotifyTrack(resultSet.getString("Id"),
                resultSet.getString("Artists"), resultSet.getString("AlbumTitle"), resultSet.getString("TrackTitle"),
                        resultSet.getString("AlbumImageUrl"), resultSet.getLong("Duration"));
        return spotifyTrack;
    }

    @Override
    public void deleteListens(final String guildId, final String userId) {
        try(final Connection connection = getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Listens WHERE GuildId=? AND UserId=?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, userId);
            preparedStatement.execute();
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }

}