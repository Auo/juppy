package com.auo.juppy;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTest {

    @Test
    public void testLoadProperties() {
        Properties properties = new Properties();
        String dbPath = "/some/path.db";
        String logbackPath = "/some/other.xml";
        properties.putAll(
                Map.of(Config.SQLITE_PATH, dbPath,
                        Config.LOGBACK_PATH, logbackPath)
        );

        Config config = new Config(properties);

        assertEquals(dbPath, config.getSqlitePath());
        assertEquals(logbackPath, config.getLogbackPath());
        assertEquals(3000, config.getPort());
    }

}