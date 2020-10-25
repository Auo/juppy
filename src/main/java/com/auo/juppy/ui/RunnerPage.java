package com.auo.juppy.ui;

import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerConfig;

import java.util.List;

public class RunnerPage extends Page {
    public final RunnerConfig config;
    public final List<RunnerResult> result;

    public RunnerPage(RunnerConfig config, List<RunnerResult> result) {

        this.config = config;
        this.result = result;
    }
}
