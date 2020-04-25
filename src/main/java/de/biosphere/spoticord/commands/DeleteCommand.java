package de.biosphere.spoticord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class DeleteCommand extends Command {

    public DeleteCommand() {
        super("delete", "Delete your stored data");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if(!message.getMentionedMembers().isEmpty() && message.getMentionedMembers().get(0).getId().equals(message.getAuthor().getId())){
            getBot().getDatabase().deleteListens(message.getGuild().getId(), message.getAuthor().getId());
            embedBuilder.setDescription("Deleted your data");
        } else {
            embedBuilder.setDescription("-delete " + message.getAuthor().getAsMention() + "\n :warning: All your data will be deleted");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

}