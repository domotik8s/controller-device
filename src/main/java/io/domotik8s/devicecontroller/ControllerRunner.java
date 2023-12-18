package io.domotik8s.devicecontroller;

import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.ControllerManager;
import io.kubernetes.client.extended.controller.LeaderElectingController;
import io.kubernetes.client.informer.SharedInformerFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ControllerRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(ControllerRunner.class);

    private final ControllerManager controllerManager;

    private final LeaderElectingController leaderElectingController;

    private final List<Controller> controllers;

    private final SharedInformerFactory informerFactory;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Shutdown signal received. Stopping controllers and informers.");

            informerFactory.stopAllRegisteredInformers();
            controllers.forEach(Controller::shutdown);
            controllerManager.shutdown();
            leaderElectingController.shutdown();
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }

            logger.debug("All resources have been gracefully shut down.");
        }));

        // Start informers and controllers
        executorService.execute(() -> {
            leaderElectingController.run();
        });
    }

}
