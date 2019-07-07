package de.biosphere.spoticord.database;


public interface DataManager extends AutoCloseable {

    SpotifyTrack getTrackData(final String trackID);

    void insertTrackData(final SpotifyTrack spotifyTrack);

    void updateTrackData(final SpotifyTrack spotifyTrack);

}
