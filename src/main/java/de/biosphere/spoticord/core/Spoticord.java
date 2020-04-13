package de.biosphere.spoticord.core;

import java.util.EnumSet;

import de.biosphere.spoticord.commands.CommandManager;
import de.biosphere.spoticord.database.Database;
import de.biosphere.spoticord.database.MySqlDatabase;
import de.biosphere.spoticord.handler.DiscordUserUpdateGameListener;
import de.biosphere.spoticord.handler.StatisticsHandlerCollector;
import io.javalin.Javalin;
import io.prometheus.client.exporter.HTTPServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
    private final Javalin javalin;

    public Spoticord() throws Exception {
        final long startTime = System.currentTimeMillis();
        logger.info("Starting spoticord");

        database = new MySqlDatabase(System.getenv("DATABASE_HOST"), System.getenv("DATABASE_USER"),
                System.getenv("DATABASE_PASSWORD"),
                System.getenv("DATABASE_NAME") == null ? "Tracks" : System.getenv("DATABASE_NAME"),
                System.getenv("DATABASE_PORT") == null ? 3306 : Integer.valueOf(System.getenv("DATABASE_PORT")));
        logger.info("Database-Connection set up!");

        jda = initializeJDA();
        logger.info("JDA set up!");

        commandManager = new CommandManager(this);
        logger.info("Command-Manager set up!");

        if (System.getenv("PROMETHEUS_PORT") != null) {
            new HTTPServer(Integer.valueOf(System.getenv("PROMETHEUS_PORT")));
            new StatisticsHandlerCollector(this).register();
            logger.info("Prometheus set up!");
        }

        javalin = Javalin.create().start(8080);
        // TODO: Javalin endpoints
        /*javalin.get("/top", context -> context.json(database.getGlobalTop(10)));
        javalin.get("/top/:id", context -> context.json(database.getTotalTop(10,
        context.pathParam("id")))); javalin.get("/random", context ->
        context.json(database.getRandomTrack()));
         
        javalin.config.addStaticFiles("src/main/resources/static", Location.EXTERNAL);
        javalin.config.addSinglePageRoot("/", "src/main/resources/static/index.html", Location.EXTERNAL);
        logger.info("Javalin set up!");*/

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdown();
            javalin.stop();
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
            jdaBuilder.setToken(System.getenv("DISCORD_TOKEN"));
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
}
