package com.auo.juppy;

import com.auo.juppy.controllers.HealthCheckController;
import com.auo.juppy.controllers.ResultController;
import com.auo.juppy.controllers.RunnerController;
import com.auo.juppy.db.Storage;
import com.auo.juppy.db.Storage.MemoryStorage;
import com.auo.juppy.db.StorageException;
import com.auo.juppy.http.BadRequestException;
import com.auo.juppy.http.ErrorResponse;
import com.auo.juppy.result.ResultHandler;
import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerManager;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

public class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    public static void main(String[] args) throws StorageException {
        //Increase log-level for jetty. Otherwise be prepared for spam!
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        Properties properties = new Properties();
        properties.setProperty("flyway.url", "jdbc:sqlite:D://temp/DATABASE.db");

        Storage storage = new Storage.SQLiteStorage(properties);
        //TODO: the capacity should probably be larger
        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);

        //TODO: send in reporters.
        ResultHandler resultHandler = new ResultHandler(queue, storage, List.of());
        RunnerManager handler = new RunnerManager(queue, storage);

        RunnerController runnerController = new RunnerController(handler);
        ResultController resultController = new ResultController(storage);

        Javalin app = Javalin.create()
                .get("health-check", HealthCheckController::status)

                .get("runners", runnerController::getAll)
                .get("runners/:id", runnerController::getOne)
                .delete("runners/:id", runnerController::delete)
                .post("runners", runnerController::create)

                .get("results/:id", resultController::get)

                .exception(BadRequestException.class, ((e, context) -> {
                    //TODO: throw this from controllers, when handled. The next exception() will be a fallback.
                    LOGGER.warn(e.getMessage(), e);
                    context.json(new ErrorResponse(e.getMessage())).status(400);
                } ))
                .exception(Exception.class, ((e, context) -> {
                    String msg = "Unexpected error: " + e.getMessage();
                    LOGGER.error(msg, e);
                    context.json(new ErrorResponse(msg)).status(500);
                }))
                .start(3000);


        // Should probably not be AutoCloseable.. but works for now.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                app.stop();
                resultHandler.close();
                handler.close();
            } catch (Exception e) {
                LOGGER.error("Error while shutting down", e);
            }
        }));
    }
}
