package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.core.Spoticord;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public abstract class Command {

    private final String command;
    private final String[] aliases;
    private final String description;
    private Spoticord bot;

    public Command(final String command, final String description, final String... alias) {
        this.command = command;
        this.description = description;
        this.aliases = alias;
    }

    public abstract void execute(final String[] args, final Message message);

    protected EmbedBuilder getEmbed(final Guild guild, final User requester) {
        return new EmbedBuilder().setFooter("@" + requester.getName() + "#" + requester.getDiscriminator(),
                requester.getEffectiveAvatarUrl()).setColor(guild.getSelfMember().getColor());
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

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public Spoticord getBot() {
        return bot;
    }
}
