package de.biosphere.spoticord.handler;

import de.biosphere.spoticord.core.Spoticord;
import de.biosphere.spoticord.database.SpotifyTrack;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.RichPresence;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordUserUpdateGameListener extends ListenerAdapter {

    private final Spoticord bot;

    public DiscordUserUpdateGameListener(final Spoticord instance) {
        this.bot = instance;
    }

    @Override
    public void onUserUpdateGame(UserUpdateGameEvent event) {
        if (event.getNewGame() != null && event.getNewGame().isRich() && event.getNewGame().getType() == Game.GameType.LISTENING) {
            if (event.getOldGame() != null && event.getOldGame().isRich() && event.getOldGame().getType() == Game.GameType.LISTENING && event.getOldGame().asRichPresence().getDetails().equalsIgnoreCase(event.getNewGame().asRichPresence().getDetails())) {
                return;
            }
            final RichPresence richPresence = event.getNewGame().asRichPresence();

            final SpotifyTrack oldTrackData = bot.getDataManager().getTrackData(richPresence.getSyncId(), event.getGuild().getId());

            if (oldTrackData == null) {
                SpotifyTrack spotifyTrack = new SpotifyTrack();
                spotifyTrack.trackId = richPresence.getSyncId();
                spotifyTrack.guildId = event.getGuild().getId();
                spotifyTrack.albumImageUrl = richPresence.getLargeImage().getUrl();
                spotifyTrack.albumTitle = richPresence.getLargeImage().getText();
                spotifyTrack.artist = richPresence.getState();
                spotifyTrack.title = richPresence.getDetails();
                spotifyTrack.totalCount = 1;
                bot.getDataManager().insertTrackData(spotifyTrack);
            } else {
                oldTrackData.totalCount++;
                bot.getDataManager().updateTrackData(oldTrackData);
            }
        }
    }
}
