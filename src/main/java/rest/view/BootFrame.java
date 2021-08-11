package rest.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kong.unirest.Unirest;
import rest.controller.plugin.concurrent.ThreadManager;
import rest.controller.plugin.stages.StageManager;
import rest.persitence.PersistEngine;
import rest.persitence.model.RestEntity;
import rest.util.CommonUtil;
import rest.util.ResUtil;
import rest.util.ViewUtil;
import rest.view.component.LoadingStage;
import rest.view.component.MenuFrame;
import rest.view.tabs.download.DownloadView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * start app init
 */
public class BootFrame extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootFrame.class);

    static {
        Font.loadFont(ResUtil.resPath("/font/CamingoCode-Regular.ttf"), 30);
        Font.loadFont(ResUtil.resPath("/font/CamingoCode-Bold.ttf"), 30);
        Font.loadFont(ResUtil.resPath("/font/CamingoCode-Italic.ttf"), 30);
        Font.loadFont(ResUtil.resPath("/font/CamingoCode-BoldItalic.ttf"), 30);
    }

    private Stage root;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {
        LOGGER.info("boot frame start begin");
        this.root = primaryStage;
        StageManager.mainStage(primaryStage);
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateMessage("loading... local");
                updateProgress(1, 10);
                PersistEngine.instance().init();
                Thread.sleep(500);
                updateProgress(3, 10);

                updateMessage("loading... register");
                registerEvent();
                Thread.sleep(500);
                updateProgress(6, 10);

                updateMessage("loading... model");
                Thread.sleep(500);
                updateProgress(8, 10);

                CompletableFuture<Void> completableFuture = new CompletableFuture<>();
                Platform.runLater(() -> {
                    BorderPane borderPane = new BorderPane();
                    borderPane.setCenter(StageManager.primaryFrame());
                    borderPane.setTop(new MenuFrame());
                    loadData();
                    root.getIcons().add(new Image(ResUtil.resPath("/icons/title.png")));
                    root.setScene(StageManager.customScene(borderPane));
                    root.setTitle(CommonUtil.TITLE);
                    Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
                    double width = screenRectangle.getWidth();
                    double height = screenRectangle.getHeight();
                    root.setMaximized(true);
                    root.setHeight(height * 0.8);
                    root.setWidth(width * 0.8);
                    completableFuture.complete(null);
                });
                completableFuture.whenComplete((aVoid, throwable) -> {
                    updateProgress(10, 10);
                    succeeded();
                });
                return "";
            }
        };
        task.setOnFailed(event -> {
            LOGGER.error("system start cause error");
            System.exit(-1);
        });
        new LoadingStage(task, root);
    }

    private void loadData() {
        List<RestEntity> reqs = PersistEngine.instance().loadEntities();
        LOGGER.info("all reqs loaded:{}", reqs);
        if (reqs.size() == 0) {
            StageManager.primaryFrame().loadEntity(PersistEngine.instance().loadExample());
        } else {
            reqs.forEach(req -> {
                if (req.isOpened()) {
                    StageManager.primaryFrame().loadEntity(req);
                }
            });
        }
    }

    private void registerEvent() {
        root.setOnCloseRequest(event -> {
            LOGGER.info("stop rest server start");
            if (DownloadView.instance().isDownloading()) {
                Alert confirmLog = new Alert(AlertType.CONFIRMATION);
                Label label = ViewUtil.customLabel("download task running,confirm to exit?");
                label.setTextFill(Color.RED);
                confirmLog.getDialogPane().setContent(label);
                ButtonType ok = ViewUtil.customDialog(confirmLog);
                confirmLog.showAndWait().ifPresent(buttonType -> {
                    if (buttonType == ok) {
                        clear();
                    } else {
                        event.consume();
                    }
                });
            } else {
                clear();
            }
        });

    }

    private void clear() {
        DownloadView.instance().close();
        StageManager.releaseAll();
        ThreadManager.close();
        StageManager.primaryFrame().close();
        Unirest.shutDown(true);
    }

}
