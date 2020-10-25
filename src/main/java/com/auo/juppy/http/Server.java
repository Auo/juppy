package com.auo.juppy.http;

import com.auo.juppy.controllers.HealthCheckController;
import com.auo.juppy.controllers.ResultController;
import com.auo.juppy.controllers.RunnerController;
import com.auo.juppy.controllers.UIController;
import com.auo.juppy.db.Storage;
import com.auo.juppy.runner.RunnerHandler;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private final Javalin app;

    public Server(RunnerHandler runnerHandler, Storage storage, int port) {
        RunnerController runnerController = new RunnerController(runnerHandler);
        ResultController resultController = new ResultController(storage);
        UIController uiController = new UIController(storage);

        this.app = Javalin.create()

                .get("health-check", HealthCheckController::status)

                .get("api/runners", runnerController::getAll)
                .get("api/runners/:id", runnerController::getOne)
                .delete("api/runners/:id", runnerController::delete)
                .post("api/runners", runnerController::create)

                .get("api/results/:id", resultController::get)

                .get("/", uiController::root)
                .get("/runners/:id", uiController::runners)

                .exception(BadRequestException.class, ((e, context) -> {
                    //TODO: throw this from controllers, when handled. The next exception() will be a fallback.
                    LOGGER.warn(e.getMessage(), e);
                    context.json(new ErrorResponse(e.getMessage())).status(400);
                }))
                .exception(Exception.class, ((e, context) -> {
                    String msg = "Unexpected error: " + e.getMessage();
                    LOGGER.error(msg, e);
                    context.json(new ErrorResponse(msg)).status(500);
                }))
                .start(port);
    }

    @Override
    public void close() throws Exception {
        app.stop();
    }
}
