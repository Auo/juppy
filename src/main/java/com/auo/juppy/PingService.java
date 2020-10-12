package com.auo.juppy;

import com.auo.juppy.db.MemoryStorage;
import com.auo.juppy.db.SQLiteStorage;
import com.auo.juppy.db.Storage;
import com.auo.juppy.result.ResultHandler;
import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerHandler;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class PingService implements AutoCloseable {
    private final Storage storage;
    private final RunnerHandler runnerHandler;
    private final ResultHandler resultHandler;

    public PingService(Config config) {

        String dbPath = config.getSqlitePath();
        this.storage = dbPath != null
                ? new SQLiteStorage(dbPath)
                : new MemoryStorage();

        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);

        // TODO: create reporters based on config
        this.resultHandler = new ResultHandler(queue, storage, List.of());
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
}
