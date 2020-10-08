package de.biosphere.spoticord.utils;

import io.prometheus.client.Gauge;

public class Metrics {

    public static final Gauge TOTAL_TRACK_AMOUNT;
    public static final Gauge TOTAL_LISTEN_AMOUNT;
    public static final Gauge TOTAL_ARTIST_AMOUNT;
    public static final Gauge TOTAL_ALBUM_AMOUNT;
    public static final Gauge CURRENT_LISTEN_MEMBERS;

    static {
        TOTAL_TRACK_AMOUNT = Gauge.build().name("total_track_amount").help("Amount of tracks").register();
        TOTAL_ARTIST_AMOUNT = Gauge.build().name("total_artist_amount").help("Amount of artists").register();
        TOTAL_ALBUM_AMOUNT = Gauge.build().name("total_album_amount").help("Amount of albums").register();
        TOTAL_LISTEN_AMOUNT = Gauge.build().name("total_listen_amount").help("Amount of listen tracks")
                .labelNames("guild").register();
        CURRENT_LISTEN_MEMBERS = Gauge.build().name("current_listen_members").help("Amount of listen members")
                .labelNames("guild").register();
    }

}
