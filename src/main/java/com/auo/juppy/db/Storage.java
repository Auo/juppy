package com.auo.juppy.db;

import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Storage {
    void saveResult(RunnerResult result);

    void createRunner(RunnerConfig config);

    void deleteRunner(UUID id);

    List<RunnerConfig> get(Set<UUID> ids);

    RunnerConfig get(UUID id);

    List<RunnerResult> getResult(UUID runnerId);

    /*private void validConfig(RunnerConfig config) {
        // does ID exist?

        // is URI already being pinged?

        // is host already being pinged?

        // allow subdomain, allow different pages on site? ( probably not )

        //TODO: throw exception if invalid.
    }*/

    class MemoryStorage implements Storage {

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
                throw new IllegalArgumentException("Can't create a runner with ID: '" + config.id + "'. It already exists");
            }

            runners.put(config.id, config);
        }

        @Override
        public void deleteRunner(UUID id) {
            runners.remove(id);
        }

        @Override
        public List<RunnerConfig> get(Set<UUID> ids) {
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
}
