package com.auo.juppy.runner;

import com.auo.juppy.db.Storage;
import com.auo.juppy.db.StorageException;
import com.auo.juppy.result.QueueItem;
import com.auo.juppy.result.RunnerResult;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RunnerHandler implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerHandler.class);

    private final ArrayBlockingQueue<QueueItem> resultQueue;
    private final Storage storage;
    private final String userAgent;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private final ConcurrentHashMap<UUID, ScheduledFuture<?>> runners = new ConcurrentHashMap<>();

    public RunnerHandler(ArrayBlockingQueue<QueueItem> resultQueue, Storage storage, @Nullable String userAgent) throws StorageException {
        this.resultQueue = resultQueue;
        this.storage = storage;
        this.userAgent = userAgent;
        storage.getAll().forEach(this::startRunner);
    }


    private void startRunner(RunnerConfig config) {
        runners.put(config.id, executor.scheduleWithFixedDelay(
                new ConnectivityRunner(config.uri, config.timeout, resultQueue, config.id, userAgent),
                0,
                config.interval,
                TimeUnit.MILLISECONDS));
    }

    public synchronized void create(RunnerConfig config) {
        storage.createRunner(config);

        startRunner(config);
    }

    public synchronized void delete(UUID id) {
        ScheduledFuture<?> scheduledFuture = runners.get(id);

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        storage.deleteRunner(id);
    }

    public RunnerConfig getOne(UUID id) {
        return storage.get(id);
    }

    public List<RunnerConfig> runners() {
        return storage.getAll();
    }

    @Override
    public void close() throws Exception {
        executor.awaitTermination(2_000, TimeUnit.MILLISECONDS);
    }


    public static class ConnectivityRunner implements Runnable {
        private final HttpClient client;
        private final String userAgent;
        private final URI uri;
        private final long timeout;
        private final ArrayBlockingQueue<QueueItem> resultQueue;
        private final UUID runnerId;

        @TestOnly
        protected ConnectivityRunner(URI uri, long timeout, ArrayBlockingQueue<QueueItem> resultQueue,
                                     UUID runnerId, HttpClient client, @Nullable String userAgent) {
            this.uri = uri;
            this.timeout = timeout;
            this.resultQueue = resultQueue;
            this.runnerId = runnerId;
            this.client = client;
            this.userAgent = userAgent;
        }

        public ConnectivityRunner(URI uri, long timeout, ArrayBlockingQueue<QueueItem> resultQueue,
                                  UUID runnerId, @Nullable String userAgent) {
            this(uri, timeout, resultQueue, runnerId, HttpClient.newHttpClient(), userAgent);
        }

        public void run() {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofMillis(timeout));

            if (userAgent != null) {
                builder = builder.header("User-Agent", userAgent);
            }

            HttpRequest request = builder.build();

            long start = System.currentTimeMillis();
            int statusCode = -1;
            try {
                HttpResponse<Void> send = client.send(request, HttpResponse.BodyHandlers.discarding());
                statusCode = send.statusCode();
            } catch (IOException | InterruptedException e) {
                LOGGER.warn("Failed to ping uri: " + uri.toString(), e);
                // TODO: Which response code should it be if it fails?
            } finally {
                resultQueue.add(
                        new QueueItem(
                                new RunnerResult(
                                        statusCode,
                                        System.currentTimeMillis() - start,
                                        runnerId,
                                        UUID.randomUUID(),
                                        Instant.now().getEpochSecond()),
                                uri)
                );
            }
        }
    }
}
