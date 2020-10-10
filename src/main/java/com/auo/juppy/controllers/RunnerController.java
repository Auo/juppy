package com.auo.juppy.controllers;


import com.auo.juppy.http.BadRequestException;
import com.auo.juppy.runner.RunnerConfig;
import com.auo.juppy.runner.RunnerManager;
import io.javalin.http.BadRequestResponse;
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

    public void getOne(Context ctx) throws BadRequestException {
        ctx.json(runnerManager.getOne(UrlUtil.getIdFromPath(ctx)));
    }

    public void delete(Context ctx) throws BadRequestException {
        runnerManager.delete(UrlUtil.getIdFromPath(ctx));

    }

    public void create(Context ctx) throws BadRequestException {
        RunnerConfig config;
        try {
            config = ctx.bodyAsClass(RunnerConfig.class);
        } catch (BadRequestResponse e) {
            throw new BadRequestException("Body is null");
        }

        config.id = UUID.randomUUID();

        try {
            config.isValid();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }

        runnerManager.create(config);
        ctx.result(config.id.toString());

    }


}
