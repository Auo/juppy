package com.auo.juppy.controllers;


import com.auo.juppy.runner.RunnerConfig;
import com.auo.juppy.runner.RunnerManager;
import io.javalin.http.Context;

import java.util.List;
import java.util.UUID;

public class RunnerController {
    private final RunnerManager runnerManager;

    public RunnerController(RunnerManager runnerManager) {
        this.runnerManager = runnerManager;
    }

    public void getAll(Context ctx) {
        List<RunnerConfig> configs = runnerManager.runners();

        ctx.json(configs);
    }

    public void getOne(Context ctx) {
        ctx.json(runnerManager.getOne(UUID.fromString(ctx.pathParam("id"))));
    }

    public void delete(Context ctx) {
        runnerManager.delete(UUID.fromString(ctx.pathParam("id")));
        ctx.status(200);
    }

    public void create(Context ctx) {
        RunnerConfig config = ctx.bodyAsClass(RunnerConfig.class);

        if (config != null) {
            config.id = UUID.randomUUID();
            runnerManager.create(config);
            ctx.status(200).result(config.id.toString());
        } else {
            ctx.status(400).result("invalid config");
        }

    }
}
