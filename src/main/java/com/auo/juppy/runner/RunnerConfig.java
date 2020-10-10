package com.auo.juppy.runner;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;
import java.util.regex.Pattern;

public class RunnerConfig {
    private static final Pattern SCHEME = Pattern.compile("http|https");

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

    public void isValid() {
        if (uri == null || !SCHEME.matcher(uri.getScheme()).matches()) {
            throw new IllegalArgumentException("Invalid URI");
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URI could not be converted to a URL");
        }

        if (uri.getHost() == null) {
            throw new IllegalArgumentException("Invalid URI, host is 0 chars");
        }

        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout is too small");
        }

        if (interval <= 0) {
            throw new IllegalArgumentException("Interval is too small");
        }
    }
}
