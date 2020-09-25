package com.auo.juppy.controllers;

import io.javalin.http.Context;

public class HealthCheckController {
    public static void status(Context ctx) {
        ctx.status(200);
    }
}
