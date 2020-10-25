package de.biosphere.spoticord.handler;

import de.biosphere.spoticord.Spoticord;
import de.biosphere.spoticord.database.model.SpotifyTrack;
import de.biosphere.spoticord.utils.Metrics;
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
    public void onUserActivityStart(@Nonnull final UserActivityStartEvent event) {
        if (checkActivity(event.getNewActivity())) {
            return;
        }
        final RichPresence richPresence = event.getNewActivity().asRichPresence();
        if (checkRichPresence(richPresence)) {
            return;
        }
        if (checkCache(event.getMember().getId(), richPresence.getSyncId())) {
            return;
        }
        lastActivitiesMap.put(event.getMember().getId(), richPresence.getSyncId());
        final SpotifyTrack spotifyTrack = new SpotifyTrack(richPresence.getSyncId(), richPresence.getState(),
                richPresence.getLargeImage().getText(), richPresence.getDetails(),
                richPresence.getLargeImage().getUrl(),
                richPresence.getTimestamps().getEnd() - richPresence.getTimestamps().getStart());
        bot.getDatabase().getTrackDao().insertTrack(spotifyTrack, event.getMember().getId(), event.getGuild().getId());
        Metrics.TRACKS_PER_MINUTE.labels(event.getGuild().getId()).inc();
    }

    private boolean checkActivity(final Activity activity) {
        return activity == null || !activity.isRich() || activity.getType() != Activity.ActivityType.LISTENING;
    }

    private boolean checkRichPresence(final RichPresence richPresence) {
        return richPresence == null || richPresence.getDetails() == null || richPresence.getSyncId() == null;
    }

    private boolean checkCache(final String memberId, final String spotifyId) {
        return lastActivitiesMap.containsKey(memberId) && lastActivitiesMap.get(memberId).equalsIgnoreCase(spotifyId);
    }
}
