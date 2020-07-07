package de.biosphere.spoticord.database;

import de.biosphere.spoticord.database.dao.AlbumDao;
import de.biosphere.spoticord.database.dao.ArtistDao;
import de.biosphere.spoticord.database.dao.TrackDao;
import de.biosphere.spoticord.database.dao.UserDao;

public interface Database extends AutoCloseable {

    AlbumDao getAlbumDao();

    ArtistDao getArtistDao();

    TrackDao getTrackDao();

    UserDao getUserDao();

}