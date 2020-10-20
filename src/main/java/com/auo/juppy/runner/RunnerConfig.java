package com.auo.juppy.runner;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import java.util.regex.Pattern;

public class RunnerConfig {

    protected static final long MIN_INTERVAL = Duration.ofMinutes(1).toMillis();
    protected static final long MAX_INTERVAL = Duration.ofDays(1).toMillis();

    protected static final long MIN_TIMEOUT = Duration.ofMillis(50).toMillis();
    protected static final long MAX_TIMEOUT = Duration.ofSeconds(15).toMillis();

    private static final Pattern SCHEME = Pattern.compile("http|https");

    public long created;
    public URI uri;
    public long timeout;
    public long interval;
    public UUID id;

    @Override
    public String toString() {
        return "uri: " + uri + "\n" +
                "timeout: " + timeout + "\n" +
                "interval: " + interval + "\n" +
                "created: " + created + "\n" +
                "id: " + (id != null ? id.toString() : "NULL") + "\n\n";
    }

    public static RunnerConfig create(URI uri, long timeout, long interval, UUID id, long created) {

        RunnerConfig config = new RunnerConfig();
        config.uri = uri;
        config.timeout = timeout;
        config.interval = interval;
        config.id = id;
        config.created = created;

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

        if (timeout < MIN_TIMEOUT) {
            throw new IllegalArgumentException("Timeout is too small, min: " + MIN_TIMEOUT);
        }

        if (timeout > MAX_TIMEOUT) {
            throw new IllegalArgumentException("Timeout is too large, max: " + MAX_TIMEOUT);
        }

        if (interval < MIN_INTERVAL) {
            throw new IllegalArgumentException("Interval is too small, min: " + MIN_INTERVAL);
        }

        if (interval > MAX_INTERVAL) {
            throw new IllegalArgumentException("Interval is too large, max: " + MAX_INTERVAL);
        }
    }
}
