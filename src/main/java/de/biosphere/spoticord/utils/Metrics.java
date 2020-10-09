package de.biosphere.spoticord.utils;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

public class Metrics {

    public static final Gauge TOTAL_TRACK_AMOUNT;
    public static final Gauge TOTAL_LISTEN_AMOUNT;
    public static final Gauge TOTAL_ARTIST_AMOUNT;
    public static final Gauge TOTAL_ALBUM_AMOUNT;
    public static final Gauge CURRENT_LISTEN_MEMBERS;
    public static final Gauge CURRENT_PEAK_TIME;

    public static final Counter TRACKS_PER_MINUTE;

    static {
        TOTAL_TRACK_AMOUNT = Gauge.build().name("total_track_amount").help("Amount of tracks").register();
        TOTAL_ARTIST_AMOUNT = Gauge.build().name("total_artist_amount").help("Amount of artists").register();
        TOTAL_ALBUM_AMOUNT = Gauge.build().name("total_album_amount").help("Amount of albums").register();
        TOTAL_LISTEN_AMOUNT = Gauge.build().name("total_listen_amount").help("Amount of listen tracks")
                .labelNames("guild").register();
        CURRENT_LISTEN_MEMBERS = Gauge.build().name("current_listen_members").help("Amount of listen members")
                .labelNames("guild").register();
        CURRENT_PEAK_TIME = Gauge.build().name("current_peak_time").help("Current peak time")
                .labelNames("guild").register();

        TRACKS_PER_MINUTE = Counter.build().name("tracks_per_minute").help("Tracks per minute")
                .labelNames("guild").register();
    }

}
