package de.biosphere.spoticord.rest;

import de.biosphere.spoticord.rest.auth.CallbackHandler;
import de.biosphere.spoticord.rest.auth.ProfileHandler;
import io.javalin.Javalin;

public class RestServer {

    private final Javalin javalin;

    public RestServer() {
        javalin = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7070);

        javalin.get("/callback", ctx -> CallbackHandler.callback(ctx));
        javalin.get("/profile", ctx -> ProfileHandler.profile(ctx));
    }
}
