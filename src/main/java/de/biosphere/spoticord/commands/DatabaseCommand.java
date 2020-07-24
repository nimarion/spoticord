package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.database.impl.mysql.MySqlDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class DatabaseCommand extends Command {

    public DatabaseCommand() {
        super("database", "Show some informations about the database");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

        final Integer trackAmount = getBot().getDatabase().getTrackDao().getTrackAmount();
        final Integer listensAmountGlobal = getBot().getDatabase().getTrackDao().getListensAmount();
        final Integer listensAmount = getBot().getDatabase().getTrackDao().getListensAmount(message.getGuild().getId());

        embedBuilder.addField("Track Datapoints", String.valueOf(trackAmount), false);
        embedBuilder.addField("Total Listens Datapoints", String.valueOf(listensAmountGlobal), false);
        embedBuilder.addField("Listens Datapoints by this Guild", String.valueOf(listensAmount), false);

        if (getBot().getDatabase() instanceof MySqlDatabase) {
            MySqlDatabase database = (MySqlDatabase) getBot().getDatabase();
            embedBuilder.addField("Track Size", database.getSizeOfTable("Tracks") + " MB", false);
            embedBuilder.addField("Listens Size", database.getSizeOfTable("Listens") + " MB", false);
        }

        message.getTextChannel().sendMessage(embedBuilder.build()).queue();

    }

}