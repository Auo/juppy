package com.auo.juppy.runner;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RunnerConfigTest {

    @Test
    public void testValid() {
        RunnerConfig config = RunnerConfig.create(URI.create("http://example.com"), 1000, 60_000, UUID.randomUUID(), -1);
        config.isValid();

        config = RunnerConfig.create(URI.create("https://www.example.com"), 1000, 60_000, UUID.randomUUID(), -1);
        config.isValid();
    }

    @Test
    public void testInvalidHost() {
        RunnerConfig config = RunnerConfig.create(URI.create("https://.com"), 1000, 60_000, UUID.randomUUID(), -1);
        assertThrows(IllegalArgumentException.class, config::isValid);
    }

    @Test
    public void testInvalidScheme() {
        RunnerConfig config = RunnerConfig.create(URI.create("file://example.com"), 1000, 60_000, UUID.randomUUID(), -1);
        assertThrows(IllegalArgumentException.class, config::isValid);
    }

    @Test
    public void testInvalidTimeout() {
        RunnerConfig config = RunnerConfig.create(URI.create("file://example.com"), RunnerConfig.MIN_TIMEOUT - 1, 1000, UUID.randomUUID(), -1);
        assertThrows(IllegalArgumentException.class, config::isValid);

        config = RunnerConfig.create(URI.create("file://example.com"), RunnerConfig.MAX_TIMEOUT + 1, 1000, UUID.randomUUID(), -1);
        assertThrows(IllegalArgumentException.class, config::isValid);
    }

    @Test
    public void testInvalidInterval() {
        RunnerConfig config = RunnerConfig.create(URI.create("file://example.com"), 1000, RunnerConfig.MIN_INTERVAL - 1, UUID.randomUUID(), -1);
        assertThrows(IllegalArgumentException.class, config::isValid);

        config = RunnerConfig.create(URI.create("file://example.com"), 1000, RunnerConfig.MAX_INTERVAL + 1, UUID.randomUUID(), -1);
        assertThrows(IllegalArgumentException.class, config::isValid);
    }

}