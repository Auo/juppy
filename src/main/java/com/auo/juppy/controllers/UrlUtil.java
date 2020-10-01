package com.auo.juppy.controllers;

import com.auo.juppy.http.BadRequestException;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UrlUtil {
    @NotNull
    public static UUID getIdFromPath(Context ctx) throws BadRequestException {
        try {
            return UUID.fromString(ctx.pathParam("id"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
