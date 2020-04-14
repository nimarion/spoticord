package de.biosphere.spoticord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats", "Show some statistics", "info");
    }

    @Override
    public void execute(String[] args, Message message) {
        final Integer trackAmount = getBot().getDatabase().getTrackAmount();
        final Integer listensAmountGlobal = getBot().getDatabase().getListensAmount(null);
        final Integer listensAmount = getBot().getDatabase().getListensAmount(message.getGuild().getId());
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

        embedBuilder.setTitle("spoticord", "https://github.com/Biospheere/spoticord");
        embedBuilder.setThumbnail(getBot().getJDA().getSelfUser().getEffectiveAvatarUrl());

        embedBuilder.addField("Track Datapoints", String.valueOf(trackAmount), false);
        embedBuilder.addField("Total Listens Datapoints", String.valueOf(listensAmountGlobal), false);
        embedBuilder.addField("Listens Datapoints by this Guild", String.valueOf(listensAmount), false);

        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

}