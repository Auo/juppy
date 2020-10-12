package com.auo.juppy;

import com.auo.juppy.config.ConfigException;
import com.auo.juppy.db.Storage;
import com.auo.juppy.result.QueueItem;
import com.auo.juppy.result.Reporter;
import com.auo.juppy.result.ResultHandler;
import com.auo.juppy.runner.RunnerHandler;

import javax.mail.internet.AddressException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

public class PingService implements AutoCloseable {
    private final Storage storage;
    private final RunnerHandler runnerHandler;
    private final ResultHandler resultHandler;

    public PingService(Config config) {

        String dbPath = config.getSqlitePath();
        this.storage = dbPath != null
                ? new Storage.SQLiteStorage(dbPath)
                : new Storage.MemoryStorage();

        ArrayBlockingQueue<QueueItem> queue = new ArrayBlockingQueue<>(2);

        List<Reporter> reporters = createReporters(config);
        this.resultHandler = new ResultHandler(queue, storage, reporters);
        this.runnerHandler = new RunnerHandler(queue, storage, config.getRunnerUserAgent());
    }

    @Override
    public void close() throws Exception {
        this.resultHandler.close();
        this.runnerHandler.close();
    }

    public RunnerHandler getRunnerHandler() {
        return runnerHandler;
    }

    public Storage getStorage() {
        return storage;
    }

    private List<Reporter> createReporters(Config config) {
        List<Reporter> reporters = new ArrayList<>();

        Properties mail = config.getMailProperties();

        if (!mail.isEmpty()) {
            try {
                reporters.add(new Reporter.EmailReporter(mail));
            } catch (AddressException e) {
                throw new ConfigException("Invalid addresses found", e);
            }
        }
        return reporters;
    }
}
