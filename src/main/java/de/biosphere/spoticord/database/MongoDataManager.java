package de.biosphere.spoticord.database;

import com.google.gson.Gson;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import net.jodah.expiringmap.ExpiringMap;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Biosphere
 * @date 27.04.18
 */
public class MongoDataManager implements DataManager {

    private final MongoDatabaseManager databaseManager;
    private final Map<String, SpotifyTrack> trackCache;
    private final JsonWriterSettings jsonWriterSettings;
    private final Gson gson;

    public MongoDataManager(final String host, final int port) {
        this.gson = new Gson();
        jsonWriterSettings = JsonWriterSettings.builder()
                .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                .build();
        databaseManager = new MongoDatabaseManager(host, port);
        final ExpiringMap.Builder<Object, Object> mapBuilder = ExpiringMap.builder();
        mapBuilder.expiration(10, TimeUnit.MINUTES).build();
        trackCache = mapBuilder.build();
    }

    @Override
    public void updateTrackData(final SpotifyTrack spotifyTrack) {
        databaseManager.getTracks().replaceOne(Filters.and(Filters.eq("trackId", spotifyTrack.trackId), Filters.eq("guildId", spotifyTrack.guildId)), Document.parse(gson.toJson(spotifyTrack)));
        trackCache.put(spotifyTrack.guildId + "-" + spotifyTrack.trackId, spotifyTrack);
    }

    @Override
    public SpotifyTrack getTrackData(final String trackId, final String guildId) {
        if (trackCache.containsKey(guildId + "-" + trackId)) {
            return trackCache.get(guildId + "-" + trackId);
        }
        final Document document = databaseManager.getTracks().find(Filters.and(Filters.eq("trackId", trackId), Filters.eq("guildId", guildId))).first();
        final SpotifyTrack spotifyTrack;
        if (document == null) {
            return null;
        } else {
            spotifyTrack = gson.fromJson(document.toJson(jsonWriterSettings), SpotifyTrack.class);
            trackCache.put(spotifyTrack.guildId + "-" + spotifyTrack.trackId, spotifyTrack);
            return spotifyTrack;
        }
    }

    @Override
    public void insertTrackData(final SpotifyTrack spotifyTrack) {
        databaseManager.getTracks().insertOne(Document.parse(gson.toJson(spotifyTrack)));
        trackCache.put(spotifyTrack.guildId + "-" + spotifyTrack.trackId, spotifyTrack);
    }

    @Override
    public List<SpotifyTrack> getGlobalTop(final int amount) {
        final List<SpotifyTrack> spotifyTracks = new ArrayList<>();
        final FindIterable<Document> document = databaseManager.getTracks().find().sort(Sorts.descending("totalCount")).limit(amount);

        for (Document aDocument : document) {
            spotifyTracks.add(gson.fromJson(aDocument.toJson(jsonWriterSettings), SpotifyTrack.class));
        }

        return spotifyTracks;
    }

    @Override
    public List<SpotifyTrack> getTotalTop(final int amount, final String guildId) {
        final List<SpotifyTrack> spotifyTracks = new ArrayList<>();
        final FindIterable<Document> document = databaseManager.getTracks().find(Filters.eq("guildId", guildId)).sort(Sorts.descending("totalCount")).limit(amount);

        for (Document aDocument : document) {
            spotifyTracks.add(gson.fromJson(aDocument.toJson(jsonWriterSettings), SpotifyTrack.class));
        }

        return spotifyTracks;
    }

    @Override
    public SpotifyTrack getRandomTrack() {
        final AggregateIterable<Document> documents = databaseManager.getTracks().aggregate(Collections.singletonList(Aggregates.sample(1)));
        if (documents.first() == null) {
            return null;
        }
        return gson.fromJson(documents.first().toJson(jsonWriterSettings), SpotifyTrack.class);
    }

    @Override
    public void close() {
        databaseManager.close();
    }
}
