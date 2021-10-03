package de.biosphere.spoticord.commands;

import java.net.URI;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import de.biosphere.spoticord.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class WebCommand extends Command {

    public WebCommand() {
        super("web", "Send link to spotify login page");
    }

    @Override
    public void execute(String[] args, Message message) {
        if (Configuration.SPOTIFY_CLIENT_ID != null && Configuration.SPOTIFY_CLIENT_SECRET != null
                && Configuration.SPOTIFY_CALLBACK_URL != null) {
            final SpotifyApi api = SpotifyApi.builder().setClientId(Configuration.SPOTIFY_CLIENT_ID)
                    .setRedirectUri(SpotifyHttpManager.makeUri(Configuration.SPOTIFY_CALLBACK_URL)).build();

            final AuthorizationCodeUriRequest authorizationCodeUriRequest = api.authorizationCodeUri()
                    .scope("user-read-private").show_dialog(true).build();
            final URI uri = authorizationCodeUriRequest.execute();

            message.getTextChannel().sendMessage(uri.toString()).queue();
        } else {
            final EmbedBuilder embedBuilder = getEmbed(message.getMember());
            embedBuilder.setDescription("Spotify Web API not enabled");
            message.getTextChannel().sendMessage(embedBuilder.build()).queue();
        }

    }

}
