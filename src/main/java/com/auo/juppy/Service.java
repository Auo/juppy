package com.auo.juppy;

import com.auo.juppy.controllers.HealthCheckController;
import com.auo.juppy.controllers.ResultController;
import com.auo.juppy.controllers.RunnerController;
import com.auo.juppy.db.Storage;
import com.auo.juppy.db.Storage.MemoryStorage;
import com.auo.juppy.result.ResultHandler;
import com.auo.juppy.result.RunnerResult;
import com.auo.juppy.runner.RunnerManager;
import io.javalin.Javalin;

import java.util.concurrent.ArrayBlockingQueue;

public class Service {
    public static void main(String[] args) {
        //Increase log-level for jetty. Otherwise be prepared for spam!
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");


        Storage storage = new MemoryStorage();
        //TODO: the capacity should probably be larger
        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);

        ResultHandler resultHandler = new ResultHandler(queue, storage);
        RunnerManager handler = new RunnerManager(queue, storage);

        RunnerController runnerController = new RunnerController(handler);
        ResultController resultController = new ResultController(storage);

        Javalin app = Javalin.create()
                .get("health-check", HealthCheckController::status)

                .get("runners", runnerController::getAll)
                .get("runners/:id", runnerController::getOne)
                .delete("runners/:id", runnerController::delete)
                .post("runners", runnerController::create)

                .get("results/:id",resultController::get)
                .exception(Exception.class, ((e, context) -> e.printStackTrace()))
                .start(3000);


        // Should probably not be AutoCloseable.. but works for now.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                app.stop();
                resultHandler.close();
                handler.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
