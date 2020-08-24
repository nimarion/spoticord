package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.utils.LastDayParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Map;

public class AlbumCommand extends Command {

    public AlbumCommand() {
        super("album", "View the top 10 album in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final LastDayParser.Parsed parsed = LastDayParser.get(args, message);

        final EmbedBuilder embedBuilder = LastDayParser.getEmbed(
                message.getGuild(), message.getAuthor(), parsed.getLastDays(), parsed.isServerStats());

        final Map<String, Integer> topAlbum = getBot().getDatabase().getAlbumDao().getTopAlbum(
                message.getGuild().getId(),
                parsed.isServerStats() ? null : parsed.getMember().getId(), 10, parsed.getLastDays());

        addListToEmbed(embedBuilder, topAlbum);
        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addListToEmbed(final EmbedBuilder embedBuilder, final Map<String, Integer> topMap) {
        embedBuilder.setTitle("Top 10 Spotify Album");

        int count = 1;
        for (Map.Entry<String, Integer> entry : topMap.entrySet()) {
            embedBuilder.appendDescription(String.format("%s. **%s** (%s)\n", count, entry.getKey(), entry.getValue()));
            count++;
        }
    }

}