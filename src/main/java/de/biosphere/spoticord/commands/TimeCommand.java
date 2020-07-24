package de.biosphere.spoticord.commands;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.biosphere.spoticord.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class TimeCommand extends Command {

    public TimeCommand() {
        super("time", "See how much time you listened to music on Spotify");
    }

    @Override
    public void execute(final String[] args, final Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (args.length == 0) {
            final Long time = getBot().getDatabase().getUserDao().getListenTime(message.getGuild().getId(), null);
            final String timeFormat = formatDuration(time);
            embedBuilder.setDescription("Der ganze Server hat bereits " + timeFormat + " Musik gehört");
        } else if (args[0].equalsIgnoreCase("server")) {
            addListToEmbed(embedBuilder, message.getGuild(),
                    getBot().getDatabase().getUserDao().getTopListenersByTime(message.getGuild().getId(), 10));
        } else if (!message.getMentionedMembers().isEmpty()) {
            final Member targetMember = DiscordUtils.getAddressedMember(message);
            final Long time = getBot().getDatabase().getUserDao().getListenTime(message.getGuild().getId(),
                    targetMember.getId());
            embedBuilder.setDescription(
                    targetMember.getAsMention() + " hat bereits " + formatDuration(time) + " Musik gehört");
        } else {
            embedBuilder.setDescription("+time [server,mention]");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addListToEmbed(final EmbedBuilder embedBuilder, final Guild guild, final Map<String, Long> topMap) {
        topMap.keySet().stream().filter(userId -> guild.getMemberById(userId) != null)
                .map(userId -> guild.getMemberById(userId)).forEach(member -> {
                    embedBuilder.appendDescription(String.format("%s#%s %s \n", member.getEffectiveName(),
                            member.getUser().getDiscriminator(), formatDuration(topMap.get(member.getId()))));
                });
    }

    private String formatDuration(long millis) {
        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        final StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Tage ");
        sb.append(hours);
        sb.append(" Stunden ");
        sb.append(minutes);
        sb.append(" Minuten ");
        sb.append(seconds);
        sb.append(" Sekunden");

        return (sb.toString());
    }

}