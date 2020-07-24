package de.biosphere.spoticord.commands;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class StatsCommand extends Command {

        public StatsCommand() {
                super("info", "Show some statistics");
        }

        @Override
        public void execute(String[] args, Message message) {
                final JDA jda = message.getJDA();
                final long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
                final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

                embedBuilder.setTitle("spoticord", "https://github.com/Biospheere/spoticord");
                embedBuilder.setThumbnail(getBot().getJDA().getSelfUser().getEffectiveAvatarUrl());

                embedBuilder.addField("JDA Version", JDAInfo.VERSION, true);
                embedBuilder.addField("Ping", jda.getGatewayPing() + "ms", true);
                embedBuilder.addField("Uptime",
                                String.valueOf(TimeUnit.MILLISECONDS.toDays(uptime) + "d "
                                                + TimeUnit.MILLISECONDS.toHours(uptime) % 24 + "h "
                                                + TimeUnit.MILLISECONDS.toMinutes(uptime) % 60 + "m "
                                                + TimeUnit.MILLISECONDS.toSeconds(uptime) % 60 + "s"),
                                true);
                embedBuilder.addField("Commands",
                                String.valueOf(getBot().getCommandManager().getAvailableCommands().size()), true);
                embedBuilder.addField("Members",
                                String.valueOf(jda.getGuilds().stream().mapToInt(Guild::getMemberCount).sum()), true);
                embedBuilder.addField("Java Version", System.getProperty("java.runtime.version").replace("+", "_"),
                                true);
                embedBuilder.addField("OS", ManagementFactory.getOperatingSystemMXBean().getName(), true);

                embedBuilder.addField("RAM Usage", (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()
                                + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()) / 1000000
                                + " / "
                                + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
                                                + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax())
                                                / 1000000
                                + " MB", true);

                message.getTextChannel().sendMessage(embedBuilder.build()).queue();
        }

}