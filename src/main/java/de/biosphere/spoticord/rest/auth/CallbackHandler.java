package de.biosphere.spoticord.rest.auth;

import java.net.URI;

import com.google.gson.JsonObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import de.biosphere.spoticord.Configuration;
import io.javalin.http.Context;

public class CallbackHandler {

    public static void callback(final Context ctx) {
        final String code = ctx.queryParam("code");
        final URI redirectURI = SpotifyHttpManager.makeUri(Configuration.SPOTIFY_CALLBACK_URL);
        try {
            final SpotifyApi api = SpotifyApi.builder().setClientId(Configuration.SPOTIFY_CLIENT_ID)
                    .setClientSecret(Configuration.SPOTIFY_CLIENT_SECRET).setRedirectUri(redirectURI).build();

            final AuthorizationCodeRequest authorizationCodeRequest = api.authorizationCode(code).build();
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            api.setAccessToken(authorizationCodeCredentials.getAccessToken());
            api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            ctx.sessionAttribute("api_object", api);

            ctx.status(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage() + redirectURI);
            ctx.result(jsonObject.toString()).status(500);
        }
    }

}
