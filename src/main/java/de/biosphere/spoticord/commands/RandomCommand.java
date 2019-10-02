package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.database.SpotifyTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class RandomCommand extends Command {

    public RandomCommand() {
        super("random", "Get a random song from this guild", "rnd");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        final SpotifyTrack randomTrack = getBot().getDataManager().getRandomTrack();
        if (randomTrack == null) {
            return;
        }
        embedBuilder.setThumbnail(randomTrack.albumImageUrl);
        embedBuilder.setTitle("Random Track");
        embedBuilder.addField("Title", randomTrack.title, true);
        embedBuilder.addField("Artist", randomTrack.artist, true);
        embedBuilder.addField("Album", randomTrack.albumTitle, true);
        embedBuilder.addField("Listener", Integer.toString(randomTrack.totalCount), true);

        message.getChannel().sendMessage(embedBuilder.build()).queue();
        message.getChannel().sendMessage("https://open.spotify.com/track/" + randomTrack.trackId).queue();
    }
}
