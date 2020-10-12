package de.biosphere.spoticord.handler;

import de.biosphere.spoticord.Spoticord;
import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatisticsHandlerCollector extends Collector {

    private static final List<String> EMPTY_LIST = new ArrayList<>();
    private final Spoticord bot;

    public StatisticsHandlerCollector(final Spoticord bot) {
        this.bot = bot;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        final long restPing = this.bot.getJDA().getRestPing().complete();
        final long gatewayPing = this.bot.getJDA().getGatewayPing();

        return Arrays.asList(
                buildGauge("discord_ping_websocket",
                        "Time in milliseconds between heartbeat and the heartbeat ack response", gatewayPing),
                buildGauge("discord_ping_rest",
                        "The time in milliseconds that discord took to respond to a REST request.", restPing),
                buildGauge("discord_guilds",
                        "Amount of all Guilds that the bot is connected to. ", this.bot.getJDA().getGuilds().size()));
    }

    private MetricFamilySamples buildGauge(String name, String help, double value) {
        return new MetricFamilySamples(name, Type.GAUGE, help,
                Collections.singletonList(new MetricFamilySamples.Sample(name, EMPTY_LIST, EMPTY_LIST, value)));
    }
}