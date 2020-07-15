package de.biosphere.spoticord.database.dao;

import java.util.Map;

public interface ArtistDao {

    /**
     * Returns a map of the artists with the most database entry on this guild. The
     * <code>code</code> argument specifies the length of the map. Map contains
     * {@link String} as name of the artists and {@link Integer} as the amount of
     * entrys in the database.
     * 
     * @param guildId the Snowflake id of the guild that the user is part of
     * @param userId  the Snowflake id of the user
     * @param count   the length of the map
     * @return A sorted map with <code>code</code> entrys
     */
    Map<String, Integer> getTopArtists(final String guildId, final String userId, final Integer count);

}