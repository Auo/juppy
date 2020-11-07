package com.auo.juppy.ui;

import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerConfig;

import java.time.Instant;
import java.util.List;

public class RunnerPage extends Page {
    public final RunnerConfig config;
    //TODO: rework this. Add a filter on how many items are going to be returned.
    public final String labels;
    public final String data;

    public RunnerPage(RunnerConfig config, List<RunnerResult> result) {

        this.config = config;
        StringBuilder labelBuilder = new StringBuilder();
        StringBuilder responseTimeBuilder = new StringBuilder();
        labelBuilder.append('[');
        responseTimeBuilder.append('[');

        for (int i = 0; i < result.size(); i++) {

            RunnerResult res = result.get(i);


            String time = Instant.ofEpochMilli(res.timestamp * 1000).toString();
            labelBuilder.append("\"").append(time).append("\"");
            responseTimeBuilder.append(res.responseTime);

            if (i + 1 < result.size()) {
                labelBuilder.append(',');
                responseTimeBuilder.append(',');
            }
        }

        labels = labelBuilder.append(']').toString();
        data = responseTimeBuilder.append(']').toString();
    }
}
