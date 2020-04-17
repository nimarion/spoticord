package de.biosphere.spoticord.database.model;

import java.sql.Date;

public record SpotifyListen(String id, Date timestamp, String guildId, String trackId, String userId) {
}