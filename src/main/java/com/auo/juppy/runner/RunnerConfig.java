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
}
