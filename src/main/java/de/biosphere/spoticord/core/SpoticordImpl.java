package de.biosphere.spoticord.core;

import de.biosphere.spoticord.commands.CommandManager;
import de.biosphere.spoticord.database.DataManager;
import de.biosphere.spoticord.database.MongoDataManager;
import de.biosphere.spoticord.database.SpotifyTrack;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.RichPresence;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpoticordImpl implements Spoticord {

    private static final Logger logger = LoggerFactory.getLogger(Spoticord.class);

    private final JDA jda;
    private final DataManager dataManager;
    private final CommandManager commandManager;


    public SpoticordImpl() throws Exception {
        final long startTime = System.currentTimeMillis();
        logger.info("Starting spoticord");

        dataManager = initializeDataManager();
        logger.info("Database-Connection set up!");

        jda = initializeJDA();
        logger.info("JDA set up!");

        commandManager = new CommandManager(this);
        logger.info("Command-Manager set up!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdown();
        }));

        logger.info(String.format("Startup finished in %dms!", System.currentTimeMillis() - startTime));
    }

    protected DataManager initializeDataManager() throws Exception {
        try {
            return new MongoDataManager(System.getenv("MONGO_HOST") == null ? "localhost" : System.getenv("MONGO_HOST"), System.getenv("MONGO_PORT") == null ? 27017 : Integer.valueOf(System.getenv("MONGO_PORT")));
        } catch (final Exception exception) {
            logger.error("Encountered exception while initializing Database-Connection!");
            throw exception;
        }
    }

    protected JDA initializeJDA() throws Exception {
        try {
            final JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
            jdaBuilder.setToken(System.getenv("DISCORD_TOKEN"));
            jdaBuilder.addEventListener(new ListenerAdapter() {
                @Override
                public void onReady(ReadyEvent event) {
                    logger.info(String.format("Logged in as %s#%s", event.getJDA().getSelfUser().getName(), event.getJDA().getSelfUser().getDiscriminator()));
                }
            });
            jdaBuilder.addEventListener(new ListenerAdapter() {
                @Override
                public void onUserUpdateGame(UserUpdateGameEvent event) {
                    if (event.getNewGame() != null && event.getNewGame().isRich() && event.getNewGame().getType() == Game.GameType.LISTENING) {
                        if (event.getOldGame() != null && event.getOldGame().isRich() && event.getOldGame().getType() == Game.GameType.LISTENING && event.getOldGame().asRichPresence().getDetails().equalsIgnoreCase(event.getNewGame().asRichPresence().getDetails())) {
                            return;
                        }
                        final RichPresence richPresence = event.getNewGame().asRichPresence();

                        final SpotifyTrack oldTrackData = dataManager.getTrackData(richPresence.getSyncId());

                        if (oldTrackData == null) {
                            SpotifyTrack spotifyTrack = new SpotifyTrack();
                            spotifyTrack.id = richPresence.getSyncId();
                            spotifyTrack.albumImageUrl = richPresence.getLargeImage().getUrl();
                            spotifyTrack.albumTitle = richPresence.getLargeImage().getText();
                            spotifyTrack.artist = richPresence.getState();
                            spotifyTrack.title = richPresence.getDetails();
                            spotifyTrack.totalCount = 1;
                            dataManager.insertTrackData(spotifyTrack);
                        } else {
                            oldTrackData.totalCount++;
                            dataManager.updateTrackData(oldTrackData);
                        }
                    }
                }
            });
            return jdaBuilder.build().awaitReady();
        } catch (Exception exception) {
            logger.error("Encountered exception while initializing ShardManager!");
            throw exception;
        }
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public DataManager getDataManager() {
        return dataManager;
    }
}
