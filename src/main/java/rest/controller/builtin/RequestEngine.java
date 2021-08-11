package rest.controller.builtin;

import javafx.application.Platform;
import rest.controller.builtin.model.Request;
import rest.controller.builtin.model.Response;
import rest.controller.builtin.process.Checker;
import rest.controller.builtin.process.Render;
import rest.controller.builtin.process.Requester;
import rest.controller.plugin.concurrent.ThreadManager;
import rest.persitence.PersistEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * util class for proc the request
 */
public class RequestEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestEngine.class);
    private final PersistEngine persistEngine = PersistEngine.instance();

    /**
     * 
     * @param item input request data
     * @param callback use to record request progress,if success or failed ,this progress will be set 1
     */
    public void procRequest(Request item, Callback callback) {
        LOGGER.info("to be proc request:{}", item);

        CompletableFuture.runAsync(() -> {
            try {
                String invalid = new Checker().process(item);
                if (invalid != null) {
                    Response response = new Response().body(invalid);
                    Platform.runLater(() -> callback.call(response));
                    return;
                }
                Response response = new Requester().process(item);
                new Render().process(response);
                Platform.runLater(() -> callback.call(response));
            } catch (Throwable e) {
                Response response = new Response().body(e.getMessage());
                Platform.runLater(() -> callback.call(response));
            }
        }, ThreadManager.requestPool());
    }
}
