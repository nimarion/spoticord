package de.biosphere.spoticord.handler;

import de.biosphere.spoticord.Spoticord;
import de.biosphere.spoticord.utils.Metrics;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.TimerTask;

public class MetricsCollector extends TimerTask {

    private final Spoticord bot;

    public MetricsCollector(final Spoticord bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        final int trackAmount = this.bot.getDatabase().getTrackDao().getTrackAmount();
        final int albumAmount = this.bot.getDatabase().getAlbumDao().getAlbumAmount();
        final int artistAmount = this.bot.getDatabase().getArtistDao().getArtistAmount();

        Metrics.TOTAL_TRACK_AMOUNT.set(trackAmount);
        Metrics.TOTAL_ALBUM_AMOUNT.set(albumAmount);
        Metrics.TOTAL_ARTIST_AMOUNT.set(artistAmount);

        for (final Guild guild : this.bot.getJDA().getGuilds()) {
            final String guildId = guild.getId();
            final int listensAmount = this.bot.getDatabase().getTrackDao().getListensAmount(guildId);
            Metrics.TOTAL_LISTEN_AMOUNT.labels(guildId).set(listensAmount);

            final long listenCount =
                    guild.getMembers().stream().map(Member::getActivities).filter(this::checkActivities).count();
            Metrics.CURRENT_LISTEN_MEMBERS.labels(guildId).set(listenCount);
        }
    }

    private boolean checkActivities(final List<Activity> activities) {
        return activities.stream().anyMatch(this::checkActivity);
    }

    private boolean checkActivity(final Activity activity) {
        return activity != null && activity.isRich() && activity.getType() == Activity.ActivityType.LISTENING;
    }

}
