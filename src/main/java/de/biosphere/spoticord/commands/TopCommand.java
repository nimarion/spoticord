package de.biosphere.spoticord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Map;

import de.biosphere.spoticord.database.model.SpotifyTrack;

public class TopCommand extends Command {

    public TopCommand() {
        super("top", "View the top 10 tracks in this guild");
    }

    @Override
    public void execute(String[] args, Message message) {

        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

        if (args.length == 0 || args[0].equalsIgnoreCase("server")) {
            final Map<SpotifyTrack, Integer> topMap = getBot().getDatabase().getTopTracks(message.getGuild().getId(),
                    null, 10);
            if (!topMap.isEmpty()) {
                embedBuilder.setThumbnail((topMap.entrySet().iterator().next().getKey().getAlbumImageUrl()));
            }
            embedBuilder.setTitle("Top 10 Spotify Tracks");

            int count = 1;
            for (SpotifyTrack spotifyTrack : topMap.keySet()) {
                embedBuilder.appendDescription(String.format("%s. **%s** by %s (%s) \n", count,
                        spotifyTrack.getTrackTitle(), spotifyTrack.getArtists(), topMap.get(spotifyTrack)));
                count++;
            }
        } else if (args[0].equalsIgnoreCase("user")) {
            final Map<String, Integer> topMap = getBot().getDatabase().getTopListeners(message.getGuild().getId(), 10);
            topMap.forEach((k, v) -> {
                final Member member = message.getGuild().getMemberById(k);
                if (member != null) {
                    embedBuilder.appendDescription(String.format("%s (%s) \n", member.getAsMention(), v));
                }
            });
        } else if (!message.getMentionedMembers().isEmpty()) {
            final Map<SpotifyTrack, Integer> topMap = getBot().getDatabase().getTopTracks(message.getGuild().getId(),
                    message.getMentionedMembers().get(0).getId(), 10);
            embedBuilder.setTitle("Top 10 Spotify Tracks");
            if (!topMap.isEmpty()) {
                embedBuilder.setThumbnail(topMap.keySet().iterator().next().getAlbumImageUrl());
            }
            int count = 1;
            for (SpotifyTrack spotifyTrack : topMap.keySet()) {
                embedBuilder.appendDescription(String.format("%s. **%s** by %s (%s) \n", count,
                        spotifyTrack.getTrackTitle(), spotifyTrack.getArtists(), topMap.get(spotifyTrack)));
                count++;
            }
        } else {
            embedBuilder.setDescription("+top [user,server,mention]");
        }

        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
