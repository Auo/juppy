package com.auo.juppy.ui;

import com.auo.juppy.runner.RunnerConfig;

import java.util.List;

public class IndexPage extends Page {

    public final List<RunnerConfig> runners;

    public IndexPage(List<RunnerConfig> runners) {
        this.runners = runners;
    }
}
