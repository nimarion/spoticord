package de.biosphere.spoticord.database;


import java.util.List;

public interface DataManager extends AutoCloseable {

    SpotifyTrack getTrackData(final String trackID, final String guildId);

    void insertTrackData(final SpotifyTrack spotifyTrack);

    void updateTrackData(final SpotifyTrack spotifyTrack);

    List<SpotifyTrack> getGlobalTop(final int amount);

    List<SpotifyTrack> getTotalTop(final int amount, final String guildId);

    SpotifyTrack getRandomTrack();

}
