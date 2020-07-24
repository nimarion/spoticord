package de.biosphere.spoticord.commands;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.biosphere.spoticord.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class ClockCommand extends Command {

    public ClockCommand() {
        super("clock", "");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        final Member member = DiscordUtils.getAddressedMember(message);
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            addTimeToEmbed(embedBuilder,
                    getBot().getDatabase().getUserDao().getMostListensTime(message.getGuild().getId()), null);
        } else {
            addTimeToEmbed(embedBuilder,
                    getBot().getDatabase().getUserDao().getMostListensTime(member.getGuild().getId(), member.getId()),
                    member);
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addTimeToEmbed(final EmbedBuilder embedBuilder, final Long date, final Member member) {
        final Date firstDateRange = new Date(date - 1800 * 1000);
        final Date secondDateRange = new Date(date + 1800 * 1000);
        final String firstRange = new SimpleDateFormat("HH.mm").format(roundToQuarter(firstDateRange));
        final String secondRange = new SimpleDateFormat("HH.mm").format(roundToQuarter(secondDateRange));
        if (member != null) {
            embedBuilder.setDescription(member.getUser().getName() + " hört am meisten Musik zwischen " + firstRange
                    + " und " + secondRange + " Uhr");
        } else {
            embedBuilder.setDescription(
                    "Der Server hört am meisten Musik zwischen " + firstRange + " und " + secondRange + " Uhr");
        }
    }

    private Date roundToQuarter(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        final int unroundedMinutes = calendar.get(Calendar.MINUTE);
        final int mod = unroundedMinutes % 15;
        calendar.add(Calendar.MINUTE, mod < 8 ? -mod : (15 - mod));
        return calendar.getTime();
    }

}