package de.biosphere.spoticord.database;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDatabaseManager implements AutoCloseable {

    private static final String DATABASE_NAME = "spoticord";
    private static final String TRACKS_COLLECTION_NAME = "tracks";

    private final MongoClient client;
    private final MongoCollection<Document> tracks;

    public MongoDatabaseManager(final String host, final int port) {
        client = new MongoClient(new ServerAddress(host, port));
        client.getAddress();
        final MongoDatabase database = client.getDatabase(DATABASE_NAME);
        tracks = database.getCollection(TRACKS_COLLECTION_NAME);
    }

    @Override
    public void close() {
        client.close();
    }

    public MongoCollection<Document> getTracks() {
        return tracks;
    }
}