package de.biosphere.spoticord.database.model;

public record SpotifyTrack(

        String id, String artists, String albumTitle, String trackTitle, String albumImageUrl, long duration

) {
}
