package de.biosphere.spoticord.enums;

import java.util.List;

public enum TrackFilter {

    TODAY(1, List.of("day", "today", "1", "heute", "tag")),
    SEVEN_DAYS(7, List.of("week", "7", "seven", "sieben", "woche")),
    THIRTY_DAYS(30, List.of("month", "30", "thirty", "dreißig", "monat")),
    CUSTOM(-1, null);

    private final int lastDayValue;
    private final List<String> alias;

    TrackFilter(final int lastDayValue, final List<String> alias) {
        this.lastDayValue = lastDayValue;
        this.alias = alias;
    }

    public int getLastDayValue() {
        return lastDayValue;
    }

    public List<String> getAlias() {
        return alias;
    }

    public static TrackFilter getFilter(final String value) {
        for (final TrackFilter trackFilter : values()) {
            if (trackFilter.name().equalsIgnoreCase(value)) return trackFilter;
            if(trackFilter.getAlias() == null) continue;
            for (final String alias : trackFilter.getAlias()) {
                if (alias.equalsIgnoreCase(value)) return trackFilter;
            }
        }
        return CUSTOM;
    }

}
