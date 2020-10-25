package de.biosphere.spoticord.database.impl.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;
import de.biosphere.spoticord.Configuration;
import de.biosphere.spoticord.database.Database;
import de.biosphere.spoticord.database.dao.AlbumDao;
import de.biosphere.spoticord.database.dao.ArtistDao;
import de.biosphere.spoticord.database.dao.TrackDao;
import de.biosphere.spoticord.database.dao.UserDao;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySqlDatabase implements Database {

    private final static String SCHEMA_FILE = "schema/db.changelog-main.xml";

    private final HikariDataSource dataSource;

    // DAOS
    private final AlbumDao albumDao;
    private final ArtistDao artistDao;
    private final TrackDao trackDao;
    private final UserDao userDao;

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
        if (Configuration.PROMETHEUS_PORT != null) {
            config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory());
        }

        dataSource = new HikariDataSource(config);

        setupLiquibaseLogger();
        updateDatabase();

        // InitDAOs
        albumDao = new AlbumImplMySql(dataSource);
        artistDao = new ArtistImplMySql(dataSource);
        trackDao = new TrackImplMySql(dataSource);
        userDao = new UserImplMySql(dataSource);
    }

    private void updateDatabase() {
        final liquibase.database.Database implementation;
        try (final Connection connection = dataSource.getConnection()) {
            implementation = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            this.liquibaseUpdate(implementation);
        } catch (final SQLException | DatabaseException ex) {
            ex.printStackTrace();
        }
    }

    private void liquibaseUpdate(final liquibase.database.Database implementation) {
        Objects.requireNonNull(implementation, "Implementation is null!");

        try (final Liquibase liquibase = new Liquibase(SCHEMA_FILE, new ClassLoaderResourceAccessor(),
                implementation)) {
            liquibase.update("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getSizeOfTable(final String table) {
        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT table_name AS `Table`, round(((data_length + index_length) / 1024 / 1024), 2) `Size in MB` FROM information_schema.TABLES WHERE table_schema = \""
                            + (Configuration.DATABASE_NAME == null ? "Tracks" : Configuration.DATABASE_NAME)
                            + "\" AND table_name = \"" + table + "\";")) {
                final ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString("Size in MB");
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public AlbumDao getAlbumDao() {
        return albumDao;
    }

    @Override
    public ArtistDao getArtistDao() {
        return artistDao;
    }

    @Override
    public TrackDao getTrackDao() {
        return trackDao;
    }

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public void close() {
        dataSource.close();
    }

    private void setupLiquibaseLogger() {
        final Logger liquibase = Logger.getLogger("liquibase");
        liquibase.setLevel(Level.SEVERE);
    }

}