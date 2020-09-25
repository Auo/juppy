package com.auo.juppy.runner;

import com.auo.juppy.db.Storage;
import com.auo.juppy.result.RunnerResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class RunnerManager implements AutoCloseable {
    private final ArrayBlockingQueue<RunnerResult> resultQueue;
    private final Storage storage;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private final ConcurrentHashMap<UUID, ScheduledFuture<?>> runners = new ConcurrentHashMap<>();

    public RunnerManager(ArrayBlockingQueue<RunnerResult> resultQueue, Storage storage) {
        this.resultQueue = resultQueue;
        this.storage = storage;
    }

    public synchronized void create(RunnerConfig config) {
        storage.createRunner(config);

        runners.put(config.id, executor.scheduleWithFixedDelay(
                new ConnectivityRunner(config.uri, config.timeout, resultQueue, config.id),
                0,
                config.interval,
                TimeUnit.MILLISECONDS));

        storage.createRunner(config);
    }

    public synchronized void delete(UUID id) {
        ScheduledFuture<?> scheduledFuture = runners.get(id);

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            storage.deleteRunner(id);
        }
    }

    public RunnerConfig getOne(UUID id) {
        return storage.get(id);
    }

    public List<RunnerConfig> runners() {
        return storage.get(runners.keySet());
    }

    @Override
    public void close() throws Exception {
        executor.awaitTermination(2_000, TimeUnit.MILLISECONDS);
    }


    public static class ConnectivityRunner implements Runnable {
        private final HttpClient client = HttpClient.newHttpClient();
        private final URI uri;
        private final long timeout;
        private final ArrayBlockingQueue<RunnerResult> resultQueue;
        private final UUID runnerId;

        public ConnectivityRunner(URI uri, long timeout, ArrayBlockingQueue<RunnerResult> resultQueue, UUID runnerId) {
            this.uri = uri;
            this.timeout = timeout;
            this.resultQueue = resultQueue;
            this.runnerId = runnerId;
        }

        public void run() {
            //TODO: Could probably resuse client for multiple requests, I guess?
            //    HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofMillis(timeout))
                    .build();

            long start = System.currentTimeMillis();
            int statusCode = -1;
            try {
                HttpResponse<Void> send = client.send(request, HttpResponse.BodyHandlers.discarding());
                statusCode = send.statusCode();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                resultQueue.add(new RunnerResult(statusCode, System.currentTimeMillis() - start, runnerId));
            }
        }
    }
}
