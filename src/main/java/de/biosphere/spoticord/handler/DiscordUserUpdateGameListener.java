package de.biosphere.spoticord.handler;

import de.biosphere.spoticord.core.Spoticord;
import de.biosphere.spoticord.database.model.SpotifyTrack;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DiscordUserUpdateGameListener extends ListenerAdapter {

    private final Spoticord bot;
    private final Map<String, String> lastActivitiesMap;

    public DiscordUserUpdateGameListener(final Spoticord instance) {
        this.bot = instance;
        this.lastActivitiesMap = ExpiringMap.builder().expiration(7, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.ACCESSED).build();
    }

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        if (event.getNewActivity() == null || !event.getNewActivity().isRich()
                || event.getNewActivity().getType() != Activity.ActivityType.LISTENING)
            return;

        final RichPresence richPresence = event.getNewActivity().asRichPresence();
        if (richPresence == null || richPresence.getDetails() == null || richPresence.getSyncId() == null)
            return;

        if (lastActivitiesMap.containsKey(event.getMember().getId())
                && lastActivitiesMap.get(event.getMember().getId()).equalsIgnoreCase(richPresence.getSyncId())) {
            return;
        }
        lastActivitiesMap.put(event.getMember().getId(), richPresence.getSyncId());

        SpotifyTrack spotifyTrack = new SpotifyTrack(richPresence.getSyncId(), richPresence.getState(),
                richPresence.getLargeImage().getText(), richPresence.getDetails(),
                richPresence.getLargeImage().getUrl(),
                richPresence.getTimestamps().getEnd() - richPresence.getTimestamps().getStart());

        bot.getDatabase().insertTrackData(spotifyTrack, event.getMember().getId(), event.getGuild().getId());
    }
}
