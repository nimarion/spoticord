package de.biosphere.spoticord.commands;

import java.util.Map;

import de.biosphere.spoticord.database.model.SpotifyTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class SongsCommand extends Command {

    public SongsCommand() {
        super("songs", "View the top 10 tracks in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (message.getMentionedMembers().isEmpty()) {
            addListToEmbed(embedBuilder,
                    getBot().getDatabase().getTrackDao().getTopTracks(message.getGuild().getId(), null, 10, 7));
        } else {
            addListToEmbed(embedBuilder, getBot().getDatabase().getTrackDao().getTopTracks(message.getGuild().getId(),
                    message.getMentionedMembers().get(0).getId(), 10, 7));
        }
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