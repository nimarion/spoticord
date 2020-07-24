package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.Spoticord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public abstract class Command {

    private final String command;
    private final String description;
    private Spoticord bot;

    public Command(final String command, final String description) {
        this.command = command;
        this.description = description;
    }

    public abstract void execute(final String[] args, final Message message);

    protected EmbedBuilder getEmbed(final Guild guild, final User requester) {
        return new EmbedBuilder().setFooter("@" + requester.getName() + "#" + requester.getDiscriminator(),
                requester.getEffectiveAvatarUrl()).setColor(guild.getSelfMember().getColor());
    }

    protected EmbedBuilder getEmbed(final Member member) {
        return new EmbedBuilder()
                .setFooter("@" + member.getUser().getName() + "#" + member.getUser().getDiscriminator(),
                        member.getUser().getEffectiveAvatarUrl())
                .setColor(member.getGuild().getSelfMember().getColor());
    }

    public void setInstance(final Spoticord instance) {
        if (bot != null) {
            throw new IllegalStateException("Can only initialize once!");
        }
        bot = instance;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public Spoticord getBot() {
        return bot;
    }
}
