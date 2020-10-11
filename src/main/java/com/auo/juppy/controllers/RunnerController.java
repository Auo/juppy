package com.auo.juppy.controllers;


import com.auo.juppy.http.BadRequestException;
import com.auo.juppy.runner.RunnerConfig;
import com.auo.juppy.runner.RunnerHandler;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

import java.util.List;
import java.util.UUID;

public class RunnerController {
    private final RunnerHandler runnerHandler;

    public RunnerController(RunnerHandler runnerHandler) {
        this.runnerHandler = runnerHandler;
    }

    public void getAll(Context ctx) {
        List<RunnerConfig> configs = runnerHandler.runners();

        ctx.json(configs);
    }

    public void getOne(Context ctx) throws BadRequestException {
        ctx.json(runnerHandler.getOne(UrlUtil.getIdFromPath(ctx)));
    }

    public void delete(Context ctx) throws BadRequestException {
        runnerHandler.delete(UrlUtil.getIdFromPath(ctx));

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

        runnerHandler.create(config);
        ctx.result(config.id.toString());

    }


}
