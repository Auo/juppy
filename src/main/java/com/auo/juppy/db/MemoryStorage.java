package com.auo.juppy.db;

import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerConfig;

import java.util.*;

public class MemoryStorage implements Storage {

    Map<UUID, RunnerConfig> runners = new HashMap<>();
    Map<UUID, List<RunnerResult>> results = new HashMap<>();

    @Override
    public void saveResult(RunnerResult result) {
        results.computeIfAbsent(result.runnerId, res -> new ArrayList<>()).add(result);
        System.out.println(result);
    }

    @Override
    public void createRunner(RunnerConfig config) {

        if (runners.containsKey(config.id)) {
            throw new StorageException("Can't create a runner with ID: '" + config.id + "'. It already exists");
        }

        runners.put(config.id, config);
    }

    @Override
    public void deleteRunner(UUID id) {
        runners.remove(id);
    }

    @Override
    public List<RunnerConfig> getAll() {
        return new ArrayList<>(runners.values());
    }

    @Override
    public RunnerConfig get(UUID id) {
        return runners.get(id);
    }

    @Override
    public List<RunnerResult> getResult(UUID runnerId) {
        return results.get(runnerId);
    }
}
