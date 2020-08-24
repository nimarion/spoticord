package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.database.model.SpotifyTrack;
import de.biosphere.spoticord.utils.LastDayParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Map;

public class SongsCommand extends Command {

    public SongsCommand() {
        super("songs", "View the top 10 tracks in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final LastDayParser.Parsed parsed = LastDayParser.get(args, message);

        final EmbedBuilder embedBuilder = LastDayParser.getEmbed(
                message.getGuild(), message.getAuthor(), parsed.getLastDays(), parsed.isServerStats());

        final Map<SpotifyTrack, Integer> topTracks = getBot().getDatabase().getTrackDao().getTopTracks(
                message.getGuild().getId(),
                parsed.isServerStats() ? null : parsed.getMember().getId(), 10, parsed.getLastDays());

        addListToEmbed(embedBuilder, topTracks);
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
}