package de.biosphere.spoticord.database.model;

import java.sql.Date;

public class SpotifyListen {

    private String id;
    private Date timestamp;
    private String guildId;
    private String trackId;
    private String userId;

    public Date getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}