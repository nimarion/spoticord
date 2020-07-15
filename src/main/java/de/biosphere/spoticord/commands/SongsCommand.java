package de.biosphere.spoticord.commands;

import java.util.Map;

import de.biosphere.spoticord.database.model.SpotifyTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class SongsCommand extends Command {

    public SongsCommand() {
        super("songs", "View the top 10 tracks in this guild", "song", "top");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (message.getMentionedMembers().isEmpty()) {
            final Map<SpotifyTrack, Integer> topMap = getBot().getDatabase().getTrackDao()
                    .getTopTracks(message.getGuild().getId(), null, 10, 7);
            if (!topMap.isEmpty()) {
                embedBuilder.setThumbnail((topMap.entrySet().iterator().next().getKey().albumImageUrl()));
            }
            embedBuilder.setTitle("Top 10 Spotify Tracks");

            int count = 1;
            for (SpotifyTrack spotifyTrack : topMap.keySet()) {
                embedBuilder.appendDescription(
                        String.format("%s. **[%s](https://open.spotify.com/track/%s)** by %s (%s) \n", count,
                                spotifyTrack.trackTitle(), spotifyTrack.id(), spotifyTrack.artists(),
                                topMap.get(spotifyTrack)));
                count++;
            }
        } else {
            final Map<SpotifyTrack, Integer> topMap = getBot().getDatabase().getTrackDao()
                    .getTopTracks(message.getGuild().getId(), message.getMentionedMembers().get(0).getId(), 10, 7);
            embedBuilder.setTitle("Top 10 Spotify Tracks");
            if (!topMap.isEmpty()) {
                embedBuilder.setThumbnail(topMap.keySet().iterator().next().albumImageUrl());
            }
            int count = 1;
            for (SpotifyTrack spotifyTrack : topMap.keySet()) {
                embedBuilder.appendDescription(
                        String.format("%s. **[%s](https://open.spotify.com/track/%s)** by %s (%s) \n", count,
                                spotifyTrack.trackTitle(), spotifyTrack.id(), spotifyTrack.artists(),
                                topMap.get(spotifyTrack)));
                count++;
            }
        }
        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

}