package com.auo.juppy;

import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static final String SQLITE_PATH = "sqlite.path";
    public static final String SERVER_PORT = "server.port";
    public static final String RUNNER_AGENT = "runner.user-agent";
    public static final String LOGBACK_PATH = "logback.path";

    public static final String MAIL_AUTH_USERNAME = "mail.auth.username";
    public static final String MAIL_AUTH_PASSWORD = "mail.auth.password";
    public static final String MAIL_FROM = "mail.from";
    public static final String MAIL_TO = "mail.to";
    // Filter out all properties that are prefixed
    private static final String MAIL_PREFIX = "mail.";

    private String sqlitePath;
    private String logbackPath;
    private int port;
    private String runnerUserAgent;
    private Properties mailProperties;

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
        this.port = Integer.parseInt(properties.getProperty(SERVER_PORT, "3000"));
        this.runnerUserAgent = properties.getProperty(RUNNER_AGENT);
        this.mailProperties = filterMailProperties(properties);
    }

    private Properties filterMailProperties(Properties properties) {
        Properties mailProperties = new Properties();
        properties.stringPropertyNames().forEach(key -> {
            if (key.startsWith(MAIL_PREFIX)) {
                mailProperties.setProperty(key, properties.getProperty(key));
            }
        });

        return mailProperties;
    }

    public String getSqlitePath() {
        return sqlitePath;
    }

    public String getLogbackPath() {
        return logbackPath;
    }

    public int getPort() {
        return port;
    }

    public String getRunnerUserAgent() {
        return runnerUserAgent;
    }

    public Properties getMailProperties() {
        return new Properties(mailProperties);
    }
}
