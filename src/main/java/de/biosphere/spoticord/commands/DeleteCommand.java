package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class DeleteCommand extends Command {

    public DeleteCommand() {
        super("delete", "Delete your stored data");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        final Member requiredMember = DiscordUtils.getRequiredMember(message, 0);
        if (requiredMember != null && requiredMember.getId().equals(message.getAuthor().getId())) {
            getBot().getDatabase().getUserDao().deleteUser(message.getGuild().getId(), message.getAuthor().getId());
            embedBuilder.setDescription("Deleted your data");
        } else {
            embedBuilder.setDescription(
                    "-delete " + message.getAuthor().getAsMention() + "\n :warning: All your data will be deleted");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

}