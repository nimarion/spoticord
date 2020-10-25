package de.biosphere.spoticord.database.dao;

import de.biosphere.spoticord.database.model.SpotifyTrack;

import java.util.List;
import java.util.Map;

public interface TrackDao {

    /**
     * @return the amount of track database entries
     */
    Integer getTrackAmount();

    /**
     * @return the amount of listen database entries
     */
    Integer getListensAmount();

    /**
     *
     * @param guildId the Snowflake id of the guild
     * @return the amount of listen database entries
     */
    Integer getListensAmount(final String guildId);

    /**
     *
     * @param guildId the Snowflake id of the guild
     * @param userId  the Snowflake id of the user
     * @return the amount of listen database entries
     */
    Integer getListensAmount(final String guildId, final String userId);

    /**
     * Insert a new listen entry into the database
     * 
     * @param spotifyTrack the {@link SpotifyTrack}
     * @param userId       the Snowflake id of the user
     * @param guildId      the Snowflake id of the guild that the user is part of
     */
    void insertTrack(final SpotifyTrack spotifyTrack, final String userId, final String guildId);

    /**
     * 
     * @param guildId  the Snowflake id of the guild
     * @param userId   the Snowflake id of the user
     * @param count    the length of the map
     * @param lastDays the last days for data collection when 0 all data
     * @return A sorted map with <code>count</code> entries
     */
    Map<SpotifyTrack, Integer> getTopTracks(final String guildId, final String userId, final Integer count,
            final Integer lastDays);

    List<SpotifyTrack> getLastTracks(final String guildId);

    List<SpotifyTrack> getLastTracks(final String guildId, final String userId);

}