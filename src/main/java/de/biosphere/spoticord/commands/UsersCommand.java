package de.biosphere.spoticord.commands;

import de.biosphere.spoticord.utils.DayParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;

public class UsersCommand extends Command {

    private static final String FOOTER_FORMAT = "%s#%s | %s day%s | %s";

    public UsersCommand() {
        super("users", "View the top 10 users in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final int lastDays = args.length == 0 ? 0 : DayParser.getDays(args[0]);

        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor(), lastDays);
        final Map<String, Integer> topMap = getBot().getDatabase().getUserDao().getTopUsers(message.getGuild().getId(),
                10, lastDays);

        topMap.forEach((k, v) -> {
            final Member member = message.getGuild().getMemberById(k);
            if (member != null) {
                embedBuilder.appendDescription(String.format("%s#%s (%s) \n", member.getEffectiveName(),
                        member.getUser().getDiscriminator(), v));
            }
        });
        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    public static EmbedBuilder getEmbed(final Guild guild, final User requester, final int days) {
        return new EmbedBuilder()
                .setFooter(
                        FOOTER_FORMAT.formatted(requester.getName(), requester.getDiscriminator(),
                                days == 0 ? "all" : days, days == 1 ? "" : "s", "Server"),
                        requester.getEffectiveAvatarUrl())
                .setColor(guild.getSelfMember().getColor());
    }

}