package de.biosphere.spoticord.handler;

import de.biosphere.spoticord.Spoticord;
import io.prometheus.client.Collector;

import java.util.ArrayList;
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
        return Collections.singletonList(
                buildGauge("discord_ping", "Time in milliseconds between heartbeat and the heartbeat ack response",
                        bot.getJDA().getGatewayPing()));
    }

    private MetricFamilySamples buildGauge(String name, String help, double value) {
        return new MetricFamilySamples(name, Type.GAUGE, help,
                Collections.singletonList(new MetricFamilySamples.Sample(name, EMPTY_LIST, EMPTY_LIST, value)));
    }
}