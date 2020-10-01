package de.biosphere.spoticord.database.dao;

import java.util.Map;

public interface UserDao {

    /**
     * Returns the time a user has listened to music
     *
     * @param guildId the Snowflake id of the guild that the user is part of
     * @param userId  the Snowflake id of the user
     * @return The time a user listened to music
     */
    Long getListenTime(final String guildId, final String userId);

    Long getListenTime(final String guildId);

    /**
     * Returns a map of the users with the most database entrys on a guild. The
     * <code>code</code> argument specifies the length of the map. Map contains
     * {@link String} as Snowflake id of a user and {@link Integer} as the amount of
     * entrys in the database.
     *
     * @param guildId the Snowflake id of the guild
     * @param count   the length of the map
     * @return A sorted map with <code>code</code> entrys
     */
    default Map<String, Integer> getTopUsers(final String guildId, final Integer count) {
        return getTopUsers(guildId, count, 0);
    }

    /**
     * Returns a map of the users with the most database entrys on a guild. The
     * <code>code</code> argument specifies the length of the map. Map contains
     * {@link String} as Snowflake id of a user and {@link Integer} as the amount of
     * entrys in the database.
     *
     * @param guildId  the Snowflake id of the guild
     * @param count    the length of the map
     * @param lastDays the last days for data collection when 0 all data
     * @return A sorted map with <code>code</code> entrys
     */
    Map<String, Integer> getTopUsers(final String guildId, final Integer count, final Integer lastDays);

    /**
     * Returns a map of the users with the highest listening time on a guild The
     * <code>code</code> argument specifies the length of the map. Map contains
     * {@link String} as Snowflake id of a user and {@link Integer} as the amount of
     * entrys to be returned.
     *
     * @param guildId the Snowflake id of the guild
     * @param count   the length of the map
     * @return A sorted map with <code>code</code> entrys
     */
    Map<String, Long> getTopListenersByTime(final String guildId, final Integer count);

    /**
     * Deletes all database entries that are linked to a specific user
     *
     * @param guildId the Snowflake id of the guild that the user is part of
     * @param userId  the Snowflake id of the user
     */
    void deleteUser(String guildId, String userId);

    /**
     * Returns the time at which most users listen to music
     *
     * @return the time in milliseconds as {@link Long}
     */
    Long getMostListensTime(final String guildId);

    Long getMostListensTime(final String guildId, final String userId);

}