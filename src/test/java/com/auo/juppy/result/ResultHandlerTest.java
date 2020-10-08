package com.auo.juppy.result;

import com.auo.juppy.db.Storage;
import com.auo.juppy.runner.RunnerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultHandlerTest {
    @Test
    @Timeout(value = 2)
    public void testPoisonRecord() throws InterruptedException {
        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);
        ResultHandler.QueueConsumer consumer = new ResultHandler.QueueConsumer(queue, new TestStorage(), List.of());

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(consumer);
        executor.shutdown();

        queue.put(ResultHandler.POISON_RECORD);

        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }
    }

    @Test
    public void testStoragePropagation() throws InterruptedException {
        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);
        RunnerResult orgResult = new RunnerResult(200, 300, UUID.randomUUID(), UUID.randomUUID());
        queue.put(orgResult);
        queue.put(ResultHandler.POISON_RECORD);


        TestStorage storage = new TestStorage();
        ResultHandler.QueueConsumer consumer = new ResultHandler.QueueConsumer(queue, storage, List.of());

        consumer.run();

        assertEquals(1, storage.results.size());
        RunnerResult storedResult = storage.results.get(0);

        assertEquals(storedResult, orgResult);

    }

    private static class TestStorage implements Storage {
        public final List<RunnerResult> results = new ArrayList<>();

        @Override
        public void saveResult(RunnerResult result) {
            results.add(result);
        }

        @Override
        public void createRunner(RunnerConfig config) {

        }

        @Override
        public void deleteRunner(UUID id) {

        }

        @Override
        public List<RunnerConfig> getAll() {
            return List.of();
        }

        @Override
        public RunnerConfig get(UUID id) {
            return null;
        }

        @Override
        public List<RunnerResult> getResult(UUID runnerId) {
            return List.of();
        }
    }
}