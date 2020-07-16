package de.biosphere.spoticord.commands;

import java.util.Map;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class UsersCommand extends Command {

    public UsersCommand() {
        super("users", "View the top 10 users in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        final Map<String, Integer> topMap = getBot().getDatabase().getUserDao().getTopUsers(message.getGuild().getId(),
                10);
        topMap.forEach((k, v) -> {
            final Member member = message.getGuild().getMemberById(k);
            if (member != null) {
                embedBuilder.appendDescription(String.format("%s#%s (%s) \n", member.getEffectiveName(),
                        member.getUser().getDiscriminator(), v));
            }
        });
        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

}