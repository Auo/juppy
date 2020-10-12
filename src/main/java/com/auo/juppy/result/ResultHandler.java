package com.auo.juppy.result;

import com.auo.juppy.db.Storage;
import com.auo.juppy.db.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class ResultHandler implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultHandler.class);
    protected final QueueConsumer consumer;
    protected static final RunnerResult POISON_RECORD = new RunnerResult(
            -1,
            -1,
            UUID.fromString("69ec27b6-83b8-427d-a8d4-027a31f33a95"),
            UUID.fromString("cf2b272f-c303-43fd-a40f-4ca134d92601"),
            -1);

    public ResultHandler(ArrayBlockingQueue<RunnerResult> queue, Storage storage, List<Reporter> reporters) {
        this.consumer = new QueueConsumer(queue, storage, reporters);
        Thread consumerThread = new Thread(consumer, "queue-consumer-thread");
        consumerThread.start();
    }

    @Override
    public void close() {
        consumer.stop();
    }

    protected static class QueueConsumer implements Runnable {
        private final ArrayBlockingQueue<RunnerResult> queue;
        private final Storage storage;
        private final List<Reporter> reporters;

        public QueueConsumer(ArrayBlockingQueue<RunnerResult> queue, Storage storage, List<Reporter> reporters) {
            this.queue = queue;
            this.storage = storage;
            this.reporters = reporters;
        }

        public void stop() {
            this.queue.add(POISON_RECORD);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    RunnerResult result = queue.take();
                    if (result.equals(POISON_RECORD)) {
                        return;
                    }
                    storage.saveResult(result);

                    if (result.statusCode % 2 != 0) {
                        reporters.forEach(r -> r.notify(result));
                    }
                } catch (InterruptedException | StorageException e) {
                    LOGGER.warn("Failed to handle result", e);
                }
            }
        }
    }
}
