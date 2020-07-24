package de.biosphere.spoticord.commands;

import java.util.Map;

import de.biosphere.spoticord.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class ArtistsCommand extends Command {

    public ArtistsCommand() {
        super("artists", "View the top 10 artists in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (args.length == 0 || args[0].equalsIgnoreCase("server")) {
            addListToEmbed(embedBuilder,
                    getBot().getDatabase().getArtistDao().getTopArtists(message.getGuild().getId(), 10));
        } else if (!message.getMentionedMembers().isEmpty()) {
            final Member member = DiscordUtils.getAddressedMember(message);
            addListToEmbed(embedBuilder, getBot().getDatabase().getArtistDao().getTopArtists(message.getGuild().getId(),
                    member.getId(), 10));
        } else {
            embedBuilder.setDescription("+artists [server,mention]");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
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