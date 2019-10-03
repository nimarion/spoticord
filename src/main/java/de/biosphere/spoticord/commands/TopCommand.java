package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.database.SpotifyTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class TopCommand extends Command {

    public TopCommand() {
        super("top", "View the top 10 tracks in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final List<SpotifyTrack> spotifyTracks = getBot().getDataManager().getTotalTop(10, message.getGuild().getId());
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (!spotifyTracks.isEmpty()) {
            embedBuilder.setThumbnail(spotifyTracks.get(0).albumImageUrl);
        }
        embedBuilder.setTitle("Top 10 Spotify Tracks");

        int count = 1;
        for (SpotifyTrack spotifyTrack : spotifyTracks) {
            embedBuilder.appendDescription(String.format("%s. **%s** by %s (%s) \n", count, spotifyTrack.title, spotifyTrack.artist, spotifyTrack.totalCount));
            count++;
        }

        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}

