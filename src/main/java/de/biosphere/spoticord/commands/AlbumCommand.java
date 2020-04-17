package de.biosphere.spoticord.commands;

import java.util.Map;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class AlbumCommand extends Command{

    public AlbumCommand() {
        super("album", "View the top 10 album in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (args.length == 0 || args[0].equalsIgnoreCase("server")) {
            final Map<String, Integer> topMap = getBot().getDatabase().getTopAlbum(message.getGuild().getId(), null,
                    10);
            embedBuilder.setTitle("Top 10 Spotify Album");

            int count = 1;
            for (Map.Entry<String, Integer> entry : topMap.entrySet()) {
                embedBuilder.appendDescription(String.format("%s. **%s** (%s)\n", count, entry.getKey(), entry.getValue()));
                count++;
            }
        } else if (!message.getMentionedMembers().isEmpty()) {
            final Map<String, Integer> topMap = getBot().getDatabase()
                    .getTopAlbum(message.getGuild().getId(), message.getMentionedMembers().get(0).getId(), 10);
            embedBuilder.setTitle("Top 10 Spotify Album");

            int count = 1;
            for (Map.Entry<String, Integer> entry : topMap.entrySet()) {
                embedBuilder.appendDescription(String.format("%s. **%s** (%s)\n", count, entry.getKey(), entry.getValue()));
                count++;
            }
        } else {
            embedBuilder.setDescription("+album [server,mention]");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
        
    }

}