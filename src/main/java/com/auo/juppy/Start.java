package com.auo.juppy;

import ch.qos.logback.classic.util.ContextInitializer;
import com.auo.juppy.db.StorageException;
import com.auo.juppy.http.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Start {
    private static Logger LOGGER;

    public static void main(String[] args) throws StorageException, IOException {
        //Increase log-level for jetty. Otherwise be prepared for spam!
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        //TODO: replace args with something more robust. picocli or jcommander?
        Config config = new Config(new File(args[0]));

        if (config.getLogbackPath() != null) {
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, config.getLogbackPath());
        }

        LOGGER = LoggerFactory.getLogger(Start.class);

        PingService pingService = new PingService(config);
        Server server = new Server(
                pingService.getRunnerHandler(),
                pingService.getStorage(),
                config.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.close();
                pingService.close();
            } catch (Exception e) {
                LOGGER.error("Error while shutting down", e);
            }
        }));
    }
}
