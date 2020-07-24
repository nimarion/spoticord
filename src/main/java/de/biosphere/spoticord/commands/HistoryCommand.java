package de.biosphere.spoticord.commands;

import java.util.List;

import de.biosphere.spoticord.DiscordUtils;
import de.biosphere.spoticord.database.model.SpotifyTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class HistoryCommand extends Command {

    public HistoryCommand() {
        super("history", "");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getMember());
        final Member member = DiscordUtils.getAddressedMember(message);
        final List<SpotifyTrack> historyTracks = getBot().getDatabase().getTrackDao()
                .getLastTracks(member.getGuild().getId(), member.getId());
        addListToEmbed(embedBuilder, historyTracks);
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addListToEmbed(final EmbedBuilder embedBuilder, final List<SpotifyTrack> historyTracks) {
        embedBuilder.setTitle("Listening History");
        if (!historyTracks.isEmpty()) {
            embedBuilder.setThumbnail(historyTracks.get(0).albumImageUrl());
        }
        int count = 1;
        for (SpotifyTrack spotifyTrack : historyTracks) {
            embedBuilder.appendDescription(String.format("%s. **[%s](https://open.spotify.com/track/%s)** by %s\n",
                    count, spotifyTrack.trackTitle(), spotifyTrack.id(), spotifyTrack.artists()));
            count++;
        }
    }

}