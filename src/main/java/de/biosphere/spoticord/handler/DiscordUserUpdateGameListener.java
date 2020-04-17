package de.biosphere.spoticord.handler;

import de.biosphere.spoticord.core.Spoticord;
import de.biosphere.spoticord.database.model.SpotifyTrack;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class DiscordUserUpdateGameListener extends ListenerAdapter {

    private final Spoticord bot;
    private final Map<Member, Activity> lastActivitiesMap;

    public DiscordUserUpdateGameListener(final Spoticord instance) {
        this.bot = instance;
        this.lastActivitiesMap = new HashMap<>();
    }

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        if (event.getNewActivity() == null || !event.getNewActivity().isRich()
                || event.getNewActivity().getType() != Activity.ActivityType.LISTENING)
            return;

        final RichPresence richPresence = event.getNewActivity().asRichPresence();
        if (richPresence == null || richPresence.getDetails() == null || richPresence.getSyncId() == null)
            return;

        if (lastActivitiesMap.containsKey(event.getMember()) && lastActivitiesMap.get(event.getMember())
                .asRichPresence().getDetails().equalsIgnoreCase(richPresence.getDetails())) {
            return;
        }
        lastActivitiesMap.put(event.getMember(), event.getNewActivity());

        SpotifyTrack spotifyTrack = new SpotifyTrack(richPresence.getSyncId(), richPresence.getState(),
                richPresence.getLargeImage().getText(), richPresence.getDetails(),
                richPresence.getLargeImage().getUrl(),
                richPresence.getTimestamps().getEnd() - richPresence.getTimestamps().getStart());

        bot.getDatabase().insertTrackData(spotifyTrack, event.getMember().getId(), event.getGuild().getId());
    }
}
