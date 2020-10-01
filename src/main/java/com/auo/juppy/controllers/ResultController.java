package com.auo.juppy.controllers;

import com.auo.juppy.db.Storage;
import com.auo.juppy.http.BadRequestException;
import io.javalin.http.Context;

public class ResultController {
    private final Storage storage;

    public ResultController(Storage storage) {
        this.storage = storage;
    }

    public void get(Context ctx) throws BadRequestException {
        ctx.json(storage.getResult(UrlUtil.getIdFromPath(ctx)));
    }

}
