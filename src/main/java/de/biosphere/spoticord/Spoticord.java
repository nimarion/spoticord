package de.biosphere.spoticord;

import java.util.EnumSet;

import de.biosphere.spoticord.commands.CommandManager;
import de.biosphere.spoticord.database.Database;
import de.biosphere.spoticord.database.impl.mysql.MySqlDatabase;
import de.biosphere.spoticord.handler.DiscordUserUpdateGameListener;
import de.biosphere.spoticord.handler.StatisticsHandlerCollector;
import io.prometheus.client.exporter.HTTPServer;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spoticord {

    private static final Logger logger = LoggerFactory.getLogger(Spoticord.class);

    private final JDA jda;
    private final Database database;
    private final CommandManager commandManager;

    public Spoticord() throws Exception {
        final long startTime = System.currentTimeMillis();
        logger.info("Starting spoticord");

        database = new MySqlDatabase(Configuration.DATABASE_HOST, Configuration.DATABASE_USER,
                Configuration.DATABASE_PASSWORD,
                Configuration.DATABASE_NAME == null ? "Tracks" : Configuration.DATABASE_NAME,
                Configuration.DATABASE_PORT== null ? 3306 : Integer.valueOf(Configuration.DATABASE_PORT));
        logger.info("Database-Connection set up!");

        jda = initializeJDA();
        logger.info("JDA set up!");

        commandManager = new CommandManager(this);
        logger.info("Command-Manager set up!");

        if (Configuration.PROMETHEUS_PORT != null) {
            new HTTPServer(Integer.valueOf(Configuration.PROMETHEUS_PORT));
            new StatisticsHandlerCollector(this).register();
            logger.info("Prometheus set up!");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdown();
            try {
                database.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }));

        logger.info(String.format("Startup finished in %dms!", System.currentTimeMillis() - startTime));
    }

    /**
     * Connect to Discord
     *
     * @return The {@link JDA} instance fot the current session
     */
    private JDA initializeJDA() throws Exception {
        try {
            final JDABuilder jdaBuilder = JDABuilder.create(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES);
            jdaBuilder.setToken(Configuration.DISCORD_TOKEN);
            if(Configuration.DISCORD_GAME != null){
                jdaBuilder.setActivity(Activity.playing(Configuration.DISCORD_GAME));
            } else {
                jdaBuilder.setActivity(Activity.playing("🎶"));
            }
            jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ONLINE);
            jdaBuilder.disableCache(EnumSet.of(CacheFlag.VOICE_STATE, CacheFlag.EMOTE));
            jdaBuilder.addEventListeners(new ListenerAdapter() {
                @Override
                public void onReady(final ReadyEvent event) {
                    logger.info(String.format("Logged in as %s#%s", event.getJDA().getSelfUser().getName(),
                            event.getJDA().getSelfUser().getDiscriminator()));
                }
            }, new DiscordUserUpdateGameListener(this));
            return jdaBuilder.build().awaitReady();
        } catch (final Exception exception) {
            logger.error("Encountered exception while initializing ShardManager!");
            throw exception;
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public Database getDatabase() {
        return database;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static void main(String... args) {
        if (System.getenv("SENTRY_DSN") != null || System.getProperty("sentry.properties") != null) {
            Sentry.init();
        }
        try {
            new Spoticord();
        } catch (Exception exception) {
            logger.error("Encountered exception while initializing the bot!", exception);
        }
    }
}
