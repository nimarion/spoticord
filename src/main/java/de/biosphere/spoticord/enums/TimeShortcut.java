package de.biosphere.spoticord.enums;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public enum TimeShortcut {

    DAY(List.of('d'), toDays(ChronoUnit.DAYS)),
    WEEK(List.of('w'), toDays(ChronoUnit.WEEKS)),
    MONTH(List.of('m'), toDays(ChronoUnit.MONTHS)),
    YEAR(List.of('y'), toDays(ChronoUnit.YEARS));

    private final List<Character> characters;
    private final int asDays;

    TimeShortcut(final List<Character> characters, final int days) {
        this.characters = characters;
        this.asDays = days;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public int asDays() {
        return asDays;
    }

    public int multiplyWith(final int value) {
        return this.asDays * value;
    }

    public static TimeShortcut getShortcut(final Character value) {
        return getShortcut(value, DAY);
    }

    public static TimeShortcut getShortcut(final Character value, final TimeShortcut defaultValue) {
        for (final TimeShortcut timeShortcut : values()) {
            if (timeShortcut.getCharacters() == null) continue;
            for (final Character character : timeShortcut.getCharacters()) {
                if (Character.toLowerCase(value) == Character.toLowerCase(character)) return timeShortcut;
            }
        }
        return defaultValue;
    }

    public static List<Character> getShortcuts() {
        final List<Character> characters = new ArrayList<>();
        for (final TimeShortcut value : values()) {
            characters.addAll(value.getCharacters());
        }
        return characters;
    }

    /**
     * @return a string with all shortcuts in a string. Like dwmy
     */
    public static String getAsString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Character shortcut : getShortcuts()) {
            stringBuilder.append(shortcut);
        }
        return stringBuilder.toString();
    }

    private static int toDays(final ChronoUnit chronoUnit) {
        final long seconds = chronoUnit.getDuration().getSeconds();
        return (int) TimeUnit.SECONDS.toDays(seconds);
    }

}
