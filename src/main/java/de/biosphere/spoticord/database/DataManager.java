package de.biosphere.spoticord.database;


import java.util.List;

public interface DataManager extends AutoCloseable {

    SpotifyTrack getTrackData(final String trackID);

    void insertTrackData(final SpotifyTrack spotifyTrack);

    void updateTrackData(final SpotifyTrack spotifyTrack);

    List<SpotifyTrack> getTotalTop(final int amount);

    SpotifyTrack getRandomTrack();

}
