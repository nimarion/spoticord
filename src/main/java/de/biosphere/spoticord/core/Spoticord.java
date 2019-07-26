package de.biosphere.spoticord.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.biosphere.spoticord.commands.CommandManager;
import de.biosphere.spoticord.database.DataManager;
import de.biosphere.spoticord.database.MongoDataManager;
import de.biosphere.spoticord.handler.DiscordUserUpdateGameListener;
import de.biosphere.spoticord.handler.StatisticsHandlerCollector;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.json.JavalinJson;
import io.prometheus.client.exporter.HTTPServer;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spoticord {

    private static final Logger logger = LoggerFactory.getLogger(Spoticord.class);

    private final JDA jda;
    private final DataManager dataManager;
    private final CommandManager commandManager;
    private final Javalin javalin;


    public Spoticord() throws Exception {
        final long startTime = System.currentTimeMillis();
        logger.info("Starting spoticord");

        dataManager = initializeDataManager();
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
        Gson gson = new GsonBuilder().create();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
        javalin.get("/top", context -> context.json(dataManager.getGlobalTop(10)));
        javalin.get("/top/:id", context -> context.json(dataManager.getTotalTop(10, context.pathParam("id"))));
        javalin.get("/random", context -> context.json(dataManager.getRandomTrack()));
        javalin.config.addStaticFiles("src/main/resources/static", Location.EXTERNAL);
        javalin.config.addSinglePageRoot("/", "src/main/resources/static/index.html", Location.EXTERNAL);
        logger.info("Javalin set up!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdown();
            javalin.stop();
        }));

        logger.info(String.format("Startup finished in %dms!", System.currentTimeMillis() - startTime));
    }

    /**
     * Connect to the database
     *
     * @return The {@link DataManager} instance
     */
    private DataManager initializeDataManager() {
        try {
            return new MongoDataManager(System.getenv("MONGO_HOST") == null ? "localhost" : System.getenv("MONGO_HOST"), System.getenv("MONGO_PORT") == null ? 27017 : Integer.valueOf(System.getenv("MONGO_PORT")));
        } catch (final Exception exception) {
            logger.error("Encountered exception while initializing Database-Connection!");
            throw exception;
        }
    }

    /**
     * Connect to Discord
     *
     * @return The {@link JDA} instance fot the current session
     */
    private JDA initializeJDA() throws Exception {
        try {
            final JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
            jdaBuilder.setToken(System.getenv("DISCORD_TOKEN"));
            jdaBuilder.addEventListener(new ListenerAdapter() {
                @Override
                public void onReady(ReadyEvent event) {
                    logger.info(String.format("Logged in as %s#%s", event.getJDA().getSelfUser().getName(), event.getJDA().getSelfUser().getDiscriminator()));
                }
            });
            jdaBuilder.addEventListener(new DiscordUserUpdateGameListener(this));
            return jdaBuilder.build().awaitReady();
        } catch (Exception exception) {
            logger.error("Encountered exception while initializing ShardManager!");
            throw exception;
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
