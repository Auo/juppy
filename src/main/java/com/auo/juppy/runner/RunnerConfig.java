package com.auo.juppy.runner;

import java.net.URI;
import java.util.UUID;

public class RunnerConfig {
    public URI uri;
    public long timeout;
    public long interval;
    public UUID id;

    @Override
    public String toString() {
        return "uri: " + uri + "\n" +
                "timeout: " + timeout + "\n" +
                "interval: " + interval + "\n" +
                "id: " + (id != null ? id.toString() : "NULL") + "\n\n";
    }

    public static RunnerConfig create(URI uri, long timeout, long interval, UUID id) {
        RunnerConfig config = new RunnerConfig();
        config.uri = uri;
        config.timeout = timeout;
        config.interval = interval;
        config.id = id;

        return config;
    }
}
