package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.Configuration;
import de.biosphere.spoticord.DiscordUtils;
import de.biosphere.spoticord.database.model.SpotifyTrack;
import de.biosphere.spoticord.enums.TrackFilter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Map;

public class SongsCommand extends Command {

    private static final Integer MAX_FETCH_DAYS;
    private static final Integer DEFAULT_DAYS;
    private static final String REGEX;

    static {
        MAX_FETCH_DAYS = DiscordUtils.getIntFromString(Configuration.MAX_FETCH_DAYS, 30);
        DEFAULT_DAYS = DiscordUtils.getIntFromString(Configuration.DEFAULT_DAYS, 7);
        REGEX = "^[+]?\\d{1,%s}+$".formatted(MAX_FETCH_DAYS.toString().length());
    }

    public SongsCommand() {
        super("songs", "View the top 10 tracks in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        final Member member = DiscordUtils.getAddressedMember(message);
        final int lastDays;
        final boolean serverStats;

        if (args.length >= 1) {
            String day = args[args.length == 1 ? 0 : 1];
            lastDays = getLastDays(day);
            serverStats = args[0].equalsIgnoreCase("server") || (args.length == 1 && day.matches(REGEX));
        } else {
            serverStats = true;
            lastDays = Math.min(DEFAULT_DAYS, MAX_FETCH_DAYS);
        }

        addListToEmbed(embedBuilder, getBot().getDatabase().getTrackDao().getTopTracks(
                message.getGuild().getId(), serverStats ? null : member.getId(), 10, lastDays),
                lastDays);

        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addListToEmbed(final EmbedBuilder embedBuilder, final Map<SpotifyTrack, Integer> topMap,
            final int days) {
        embedBuilder.setTitle("Top 10 Spotify Tracks over %s day%s".formatted(days, days == 1 ? "" : "s"));
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

}