package de.biosphere.spoticord.enums;

import java.util.List;

public enum TimeFilter {

    TODAY(1, List.of("day", "today", "1", "heute", "tag")),
    SEVEN_DAYS(7, List.of("week", "7", "seven", "sieben", "woche")),
    THIRTY_DAYS(30, List.of("month", "30", "thirty", "dreißig", "monat")),
    CUSTOM(-1, null);

    private final int lastDayValue;
    private final List<String> alias;

    TimeFilter(final int lastDayValue, final List<String> alias) {
        this.lastDayValue = lastDayValue;
        this.alias = alias;
    }

    public int getLastDayValue() {
        return lastDayValue;
    }

    public List<String> getAlias() {
        return alias;
    }

    public static TimeFilter getFilter(final String value) {
        for (final TimeFilter timeFilter : values()) {
            if (timeFilter.name().equalsIgnoreCase(value)) return timeFilter;
            if(timeFilter.getAlias() == null) continue;
            for (final String alias : timeFilter.getAlias()) {
                if (alias.equalsIgnoreCase(value)) return timeFilter;
            }
        }
        return CUSTOM;
    }

}
