package de.biosphere.spoticord.commands;

import java.util.Map;

import de.biosphere.spoticord.DiscordUtils;
import de.biosphere.spoticord.database.model.SpotifyTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class SongsCommand extends Command {

    public SongsCommand() {
        super("songs", "View the top 10 tracks in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        final Member member = DiscordUtils.getAddressedMember(message);
        if (args.length == 0 || args[0].equalsIgnoreCase("server")) {
            addListToEmbed(embedBuilder,
                    getBot().getDatabase().getTrackDao().getTopTracks(message.getGuild().getId(), null, 10, 7));
        } else {
            addListToEmbed(embedBuilder, getBot().getDatabase().getTrackDao().getTopTracks(message.getGuild().getId(),
                    member.getId(), 10, 7));
        }
        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addListToEmbed(final EmbedBuilder embedBuilder, final Map<SpotifyTrack, Integer> topMap) {
        embedBuilder.setTitle("Top 10 Spotify Tracks");
        if (!topMap.isEmpty()) {
            embedBuilder.setThumbnail(topMap.keySet().iterator().next().albumImageUrl());
        }
        int count = 1;
        for (SpotifyTrack spotifyTrack : topMap.keySet()) {
            embedBuilder.appendDescription(String.format(
                    "%s. **[%s](https://open.spotify.com/track/%s)** by %s (%s) \n", count, spotifyTrack.trackTitle(),
                    spotifyTrack.id(), spotifyTrack.artists(), topMap.get(spotifyTrack)));
            count++;
        }
    }

}