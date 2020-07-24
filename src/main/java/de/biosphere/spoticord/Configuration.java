package de.biosphere.spoticord;

import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;

public class Configuration {

    public static final String DATABASE_HOST;
    public static final String DATABASE_USER;
    public static final String DATABASE_PASSWORD;
    public static final String DATABASE_NAME;
    public static final String DATABASE_PORT;

    public static final String PROMETHEUS_PORT;
    public static final String DISCORD_TOKEN;
    public static final String DISCORD_GAME;
    public static final String DISCORD_PREFIX;

    static {
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        DATABASE_HOST = getenv("DATABASE_HOST", dotenv);
        DATABASE_USER = getenv("DATABASE_USER", dotenv);
        DATABASE_PASSWORD = getenv("DATABASE_PASSWORD", dotenv);
        DATABASE_PORT = getenv("DATABASE_PORT", dotenv);
        DATABASE_NAME = getenv("DATABASE_NAME", dotenv);

        PROMETHEUS_PORT = getenv("PROMETHEUS_PORT", dotenv);
        DISCORD_TOKEN = getenv("DISCORD_TOKEN", dotenv);
        DISCORD_GAME = getenv("DISCORD_GAME", dotenv);
        DISCORD_PREFIX = getenv("DISCORD_PREFIX", dotenv);

        try {
            checkNull();
            LoggerFactory.getLogger(Configuration.class).info("Configuration loaded!");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static String getenv(final String name, final Dotenv dotenv) {
        if (System.getenv(name) != null) {
            return System.getenv(name);
        } else if (dotenv.get(name) != null) {
            return dotenv.get(name);
        }
        return null;
    }

    private static void checkNull() throws IllegalAccessException {
        for (Field f : Configuration.class.getDeclaredFields()) {
            LoggerFactory.getLogger(Configuration.class).debug(f.getName() + " environment variable "
                    + (f.get(Configuration.class) == null ? "is null" : "has been loaded"));
        }
    }

}