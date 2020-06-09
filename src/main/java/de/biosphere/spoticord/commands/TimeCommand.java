package de.biosphere.spoticord.commands;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class TimeCommand extends Command {

    public TimeCommand() {
        super("time", "See how much time you listened to music on Spotify", "lt");
    }

    @Override
    public void execute(final String[] args, final Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (args.length == 0) {
            final Long time = getBot().getDatabase().getListenTime(message.getGuild().getId(), null);
            final String timeFormat = formatDuration(time);
            embedBuilder.setDescription("Der ganze Server hat bereits " + timeFormat + " Musik gehört");
        } else if (args[0].equalsIgnoreCase("server")) {
            final Map<String, Long> topMap = getBot().getDatabase().getTopListenersByTime(message.getGuild().getId(),
                    10);
            topMap.forEach((k, v) -> {
                final Member member = message.getGuild().getMemberById(k);
                if (member != null) {
                    embedBuilder
                            .appendDescription(String.format("%s#%s %s \n", member.getEffectiveName(), member.getUser().getDiscriminator(), formatDuration(v)));
                }
            });
        } else if (!message.getMentionedMembers().isEmpty()) {
            final Member targetMember = message.getMentionedMembers().get(0);
            final Long time = getBot().getDatabase().getListenTime(message.getGuild().getId(), targetMember.getId());
            final String timeFormat = formatDuration(time);
            embedBuilder.setDescription(targetMember.getAsMention() + " hat bereits " + timeFormat + " Musik gehört");
        } else {
            embedBuilder.setDescription("+time [server,mention]");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
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