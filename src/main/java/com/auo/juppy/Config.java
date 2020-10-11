package com.auo.juppy;

import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class Config {
    public static final String SQLITE_PATH = "sqlite.path";
    public static final String SERVER_PORT = "server.port";
    //TODO: implement these
    public static final String LOGBACK_PATH = "logback.path";
    public static final String RESULT_DURATION = "result.duration";
    // smtp info
    // sms?

    private String sqlitePath;
    private Duration resultKeepDuration;
    private String logbackPath;
    private int port;

    public Config(File file) throws IOException {
        Properties props = new Properties();

        try (InputStream is = new FileInputStream(file)) {
            props.load(is);
        }

        validate(props);
    }

    @TestOnly
    public Config(Properties properties) {
        validate(properties);
    }

    protected void validate(Properties properties) {
        this.sqlitePath = properties.getProperty(SQLITE_PATH);
        this.logbackPath = properties.getProperty(LOGBACK_PATH);
        this.resultKeepDuration = Duration.parse(properties.getProperty(RESULT_DURATION, "P7D"));
        this.port = Integer.parseInt(properties.getProperty(SERVER_PORT, "3000"));
    }

    public String getSqlitePath() {
        return sqlitePath;
    }

    public Duration getResultKeepDuration() {
        return resultKeepDuration;
    }

    public String getLogbackPath() {
        return logbackPath;
    }

    public int getPort() {
        return port;
    }
}
