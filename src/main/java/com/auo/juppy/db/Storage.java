package com.auo.juppy.db;

import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerConfig;

import java.util.List;
import java.util.UUID;

public interface Storage {
    void saveResult(RunnerResult result);

    void createRunner(RunnerConfig config);

    void deleteRunner(UUID id);

    List<RunnerConfig> getAll();

    RunnerConfig get(UUID id);

    List<RunnerResult> getResult(UUID runnerId);

}
