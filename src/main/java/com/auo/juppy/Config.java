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
    //TODO: implement these
    public static final String LOGBACK_PATH = "logback.path";
    public static final String RESULT_DURATION = "result.duration";
    // smtp info
    // sms?

    private String sqlitePath;
    private Duration resultKeepDuration;
    private String logbackPath;

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


    // Config should then be used to create a wrapper obnject which holds reporters and similar.
}
