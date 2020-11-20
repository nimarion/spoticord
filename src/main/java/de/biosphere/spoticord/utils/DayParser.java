package de.biosphere.spoticord.utils;

import de.biosphere.spoticord.Configuration;
import de.biosphere.spoticord.enums.TimeFilter;
import de.biosphere.spoticord.enums.TimeShortcut;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayParser {

    private static final Integer MAX_FETCH_DAYS;
    private static final Integer DEFAULT_DAYS;
    private static final TimeShortcut DEFAULT_SHORTCUT;
    private static final Integer MIN_DAYS;
    private static final String REGEX;
    private static final Pattern SELECT_PATTERN;
    private static final Pattern MENTION_PATTERN;

    private static final String FOOTER_FORMAT = "%s#%s | %d day%s | %s";

    static {
        MAX_FETCH_DAYS = DiscordUtils.getIntFromString(Configuration.MAX_FETCH_DAYS, 30);
        DEFAULT_DAYS = DiscordUtils.getIntFromString(Configuration.DEFAULT_DAYS, 7);
        DEFAULT_SHORTCUT = TimeShortcut.DAY;
        MIN_DAYS = Math.min(DEFAULT_DAYS, MAX_FETCH_DAYS);
        REGEX = "(?i)^[+]?(\\d{1,%s})([%s])?$".formatted(MAX_FETCH_DAYS.toString().length(), TimeShortcut.getAsString());
        SELECT_PATTERN = Pattern.compile(REGEX);
        MENTION_PATTERN = Message.MentionType.USER.getPattern();
    }

    public static int getDays(final String input) {
        final TimeFilter filter = TimeFilter.getFilter(input);
        if (filter != TimeFilter.CUSTOM)
            return Math.min(filter.getDayValue(), MAX_FETCH_DAYS);
        final Matcher matcher = SELECT_PATTERN.matcher(input);
        if (!matcher.matches())
            return MIN_DAYS;
        final TimeShortcut timeShortcut =
                matcher.group(2) != null ? TimeShortcut.getShortcut(matcher.group(2).charAt(0)) : DEFAULT_SHORTCUT;
        final int parseInt = Integer.parseInt(matcher.group(1));
        final int days = timeShortcut.multiplyWith(parseInt);
        if (days <= 0)
            return MIN_DAYS;
        return Math.min(days, MAX_FETCH_DAYS);
    }

    public static EmbedBuilder getEmbed(final Guild guild, final User requester, final int days,
            final boolean serverStats) {
        return new EmbedBuilder()
                .setFooter(
                        FOOTER_FORMAT.formatted(requester.getName(), requester.getDiscriminator(), days,
                                days == 1 ? "" : "s", serverStats ? "Server" : "User"),
                        requester.getEffectiveAvatarUrl())
                .setColor(guild.getSelfMember().getColor());
    }

    public static Parsed get(final String[] args, final Message message) {
        final Member member = DiscordUtils.getAddressedMember(message);
        final int days;
        final boolean serverStats;

        if (args.length >= 1) {
            String day = args[args.length == 1 ? 0 : 1];
            days = getDays(day);
            serverStats = !MENTION_PATTERN.matcher(args[0]).matches();
        } else {
            serverStats = true;
            days = MIN_DAYS;
        }
        return new Parsed(member, days, serverStats);
    }

    public static class Parsed {

        private final Member member;
        private final int days;
        private final boolean serverStats;

        public Parsed(final Member member, final int days, final boolean serverStats) {
            this.member = member;
            this.days = days;
            this.serverStats = serverStats;
        }

        public Member getMember() {
            return member;
        }

        public int getDays() {
            return days;
        }

        public boolean isServerStats() {
            return serverStats;
        }
    }

}
