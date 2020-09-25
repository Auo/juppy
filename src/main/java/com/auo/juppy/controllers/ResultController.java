package com.auo.juppy.controllers;

import com.auo.juppy.db.Storage;
import io.javalin.http.Context;

import java.util.UUID;

public class ResultController {
    private final Storage storage;

    public ResultController(Storage storage) {
        this.storage = storage;
    }

    public void get(Context ctx) {
        ctx.json(storage.getResult(UUID.fromString(ctx.pathParam("id"))));
    }

}
