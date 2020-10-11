package com.auo.juppy;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void testLoadProperties() {
        Properties properties = new Properties();
        String dbPath = "/some/path.db";
        String logbackPath = "/some/other.xml";
        String duration = "P2D";
        properties.putAll(
                Map.of(Config.SQLITE_PATH, dbPath,
                        Config.LOGBACK_PATH, logbackPath,
                        Config.RESULT_DURATION, duration)
        );

        Config config = new Config(properties);

        assertEquals(Duration.parse(duration), config.getResultKeepDuration());
        assertEquals(dbPath, config.getSqlitePath());
        assertEquals(logbackPath, config.getLogbackPath());
    }

}