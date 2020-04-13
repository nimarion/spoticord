package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.database.model.SpotifyTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class RandomCommand extends Command {

    public RandomCommand() {
        super("random", "Get a random song from this guild", "rnd");
    }

    @Override
    public void execute(final String[] args, final Message message) {
        final SpotifyTrack spotifyTrack = getBot().getDatabase().getRandomTrack(message.getGuild().getId());
        if (spotifyTrack == null) {
            return;
        }
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        embedBuilder.setThumbnail(spotifyTrack.getAlbumImageUrl());
        embedBuilder.setTitle("Random Track");
        embedBuilder.addField("Title", spotifyTrack.getTrackTitle(), true);
        embedBuilder.addField("Artist", spotifyTrack.getArtists(), true);
        embedBuilder.addField("Album", spotifyTrack.getAlbumTitle(), true);
        message.getChannel().sendMessage(embedBuilder.build()).queue();
        message.getChannel().sendMessage("https://open.spotify.com/track/" + spotifyTrack.getId()).queue();
    }
}
