package de.biosphere.spoticord.database;

import java.util.Map;

import de.biosphere.spoticord.database.model.SpotifyTrack;

public interface Database extends AutoCloseable {

    void insertTrackData(final SpotifyTrack spotifyTrack, final String userId, final String guildId);

    SpotifyTrack getRandomTrack(final String guildId);

    Integer getTrackAmount();

    Integer getListensAmount(final String guildId);

    Long getListenTime(final String guildId, final String userId);

    Map<String, Integer> getTopListeners(final String guildId, final Integer count);

    Map<String, Long> getTopListenersByTime(final String guildId, final Integer count);

    Map<SpotifyTrack, Integer> getTopTracks(final String guildId, final String userId, final Integer count);

    Map<String, Integer> getTopArtists(final String guildId, final String userId, final Integer count);

    Map<String, Integer> getTopAlbum(final String guildId, final String userId, final Integer count);

    void deleteListens(final String guildId, final String userId);

}