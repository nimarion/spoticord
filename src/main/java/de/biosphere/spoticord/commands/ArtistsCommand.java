package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.utils.DayParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Map;

public class ArtistsCommand extends Command {

    public ArtistsCommand() {
        super("artists", "View the top 10 artists in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final DayParser.Parsed parsed = DayParser.get(args, message);

        final EmbedBuilder embedBuilder = DayParser.getEmbed(message.getGuild(), message.getAuthor(), parsed.getDays(),
                parsed.isServerStats());

        final Map<String, Integer> topArtists = getBot().getDatabase().getArtistDao().getTopArtists(
                message.getGuild().getId(), parsed.isServerStats() ? null : parsed.getMember().getId(), 10,
                parsed.getDays());

        addListToEmbed(embedBuilder, topArtists);
        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addListToEmbed(final EmbedBuilder embedBuilder, final Map<String, Integer> topMap) {
        embedBuilder.setTitle("Top 10 Spotify Artists");

        int count = 1;
        for (Map.Entry<String, Integer> entry : topMap.entrySet()) {
            embedBuilder.appendDescription(String.format("%s. **%s** (%s)\n", count, entry.getKey(), entry.getValue()));
            count++;
        }
    }

}