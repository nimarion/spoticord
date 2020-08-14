package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.Configuration;
import de.biosphere.spoticord.DiscordUtils;
import de.biosphere.spoticord.database.model.SpotifyTrack;
import de.biosphere.spoticord.enums.TrackFilter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;
import java.util.regex.Pattern;

public class SongsCommand extends Command {

    private static final Integer MAX_FETCH_DAYS;
    private static final Integer DEFAULT_DAYS;
    private static final String REGEX;
    private static final Pattern MENTION_PATTERN;

    static {
        MAX_FETCH_DAYS = DiscordUtils.getIntFromString(Configuration.MAX_FETCH_DAYS, 30);
        DEFAULT_DAYS = DiscordUtils.getIntFromString(Configuration.DEFAULT_DAYS, 7);
        REGEX = "^[+]?\\d{1,%s}+$".formatted(MAX_FETCH_DAYS.toString().length());
        MENTION_PATTERN = Message.MentionType.USER.getPattern();
    }

    public SongsCommand() {
        super("songs", "View the top 10 tracks in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final Member member = DiscordUtils.getAddressedMember(message);
        final int lastDays;
        final boolean serverStats;

        if (args.length >= 1) {
            String day = args[args.length == 1 ? 0 : 1];
            lastDays = getLastDays(day);
            serverStats = !MENTION_PATTERN.matcher(args[0]).matches();
        } else {
            serverStats = true;
            lastDays = Math.min(DEFAULT_DAYS, MAX_FETCH_DAYS);
        }

        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor(), lastDays, serverStats);
        addListToEmbed(embedBuilder, getBot().getDatabase().getTrackDao().getTopTracks(
                message.getGuild().getId(), serverStats ? null : member.getId(), 10, lastDays));

        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addListToEmbed(final EmbedBuilder embedBuilder, final Map<SpotifyTrack, Integer> topMap) {
        embedBuilder.setTitle("Top 10 Spotify Tracks");
        if (!topMap.isEmpty()) {
            embedBuilder.setThumbnail(topMap.keySet().iterator().next().albumImageUrl());
        }
        int count = 1;
        for (SpotifyTrack spotifyTrack : topMap.keySet()) {
            embedBuilder.appendDescription(String.format(
                    "%s. **[%s](https://open.spotify.com/track/%s)** by %s (%s) \n", count, spotifyTrack.trackTitle(),
                    spotifyTrack.id(), spotifyTrack.artists(), topMap.get(spotifyTrack)));
            count++;
        }
    }

    private int getLastDays(final String input) {
        final TrackFilter filter = TrackFilter.getFilter(input);
        if (filter != TrackFilter.CUSTOM) return Math.min(filter.getLastDayValue(), MAX_FETCH_DAYS);
        if (!input.matches(REGEX)) return Math.min(DEFAULT_DAYS, MAX_FETCH_DAYS);
        final int days = Integer.parseInt(input);
        if (days <= 0) return Math.min(DEFAULT_DAYS, MAX_FETCH_DAYS);
        return Math.min(days, MAX_FETCH_DAYS);
    }

    private EmbedBuilder getEmbed(final Guild guild, final User requester, final int days, final boolean serverStats) {
        return new EmbedBuilder().setFooter("@%s#%s | %d day%s | %s"
                        .formatted(requester.getName(), requester.getDiscriminator(),
                                days, days == 1 ? "" : "s", serverStats ? "Server" : "User"),
                requester.getEffectiveAvatarUrl()).setColor(guild.getSelfMember().getColor());
    }
}