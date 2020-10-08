package de.biosphere.spoticord.utils;

import io.prometheus.client.Gauge;

public class Metrics {

    public static final Gauge TOTAL_TRACK_COUNT;
    public static final Gauge TOTAL_LISTEN_COUNT;
    public static final Gauge CURRENT_LISTEN_MEMBERS;

    static {
        TOTAL_TRACK_COUNT = Gauge.build().name("total_track_count").help("Count of tracks").register();
        TOTAL_LISTEN_COUNT = Gauge.build().name("total_listen_count").help("Count of listen tracks")
                .labelNames("guild").register();
        CURRENT_LISTEN_MEMBERS = Gauge.build().name("current_listen_members").help("Count of listen members")
                .labelNames("guild").register();
    }

}
