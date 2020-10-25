package com.auo.juppy.controllers;

import com.auo.juppy.db.Storage;
import com.auo.juppy.http.BadRequestException;
import com.auo.juppy.ui.IndexPage;
import com.auo.juppy.ui.RunnerPage;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

public class UIController {
    private final Storage storage;

    public UIController(Storage storage) {
        this.storage = storage;
    }

    public void root(Context ctx) {
        ctx.render("index.jte", Map.of("indexPage", new IndexPage(storage.getAll())));
    }

    public void runners(Context ctx) throws BadRequestException {
        UUID id = UrlUtil.getIdFromPath(ctx);
        ctx.render("runners.jte", Map.of("runnerPage", new RunnerPage(storage.get(id), storage.getResult(id))));
    }
}
