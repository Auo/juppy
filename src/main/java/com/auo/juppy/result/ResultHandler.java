package com.auo.juppy.result;

import com.auo.juppy.db.Storage;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class ResultHandler implements AutoCloseable {
    QueueConsumer consumer;
    private static final RunnerResult POISON_RECORD = new RunnerResult(-1, -1, UUID.fromString("69ec27b6-83b8-427d-a8d4-027a31f33a95"));
    // TODO: list of reporters to notify when error occurs.

    public ResultHandler(ArrayBlockingQueue<RunnerResult> queue, Storage storage) {
        this.consumer = new QueueConsumer(queue, storage);
        Thread consumerThread = new Thread(consumer, "queue-consumer-thread");
        consumerThread.start();
    }

    @Override
    public void close() {
        consumer.stop();
    }

    private static class QueueConsumer implements Runnable {
        private final ArrayBlockingQueue<RunnerResult> queue;
        private final Storage storage;

        public QueueConsumer(ArrayBlockingQueue<RunnerResult> queue, Storage storage) {
            this.queue = queue;
            this.storage = storage;
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
                } catch (InterruptedException e) {
                    //TODO: log this, should keep eating items even if one is failed.
                    e.printStackTrace();
                }
            }
        }
    }
}
