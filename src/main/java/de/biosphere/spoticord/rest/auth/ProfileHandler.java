package de.biosphere.spoticord.rest.auth;

import com.google.gson.JsonObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import io.javalin.http.Context;

public class ProfileHandler {

    public static void profile(final Context ctx) {
        try {
            final SpotifyApi api = (SpotifyApi) ctx.sessionAttribute("api_object");

            final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = api.getCurrentUsersProfile().build();
            final User user = getCurrentUsersProfileRequest.execute();

            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("display_name", user.getDisplayName());
            jsonObject.addProperty("id", user.getId());

            ctx.result(jsonObject.toString());
        } catch (Exception e) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());
            ctx.result(jsonObject.toString()).status(500);
        }
    }

}
