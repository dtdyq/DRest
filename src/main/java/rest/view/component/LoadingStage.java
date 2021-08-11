

package rest.view.component;

import com.jfoenix.controls.JFXProgressBar;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rest.controller.plugin.stages.StageManager;
import rest.view.BootFrame;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class LoadingStage {
    public LoadingStage(Task<String>task, Stage main) throws IOException {

        Parent r = FXMLLoader.load(BootFrame.class.getResource("/loader.fxml"));
        JFXProgressBar progressBar = (JFXProgressBar) r.lookup("#progressBar");
        progressBar.getStyleClass().add("success");
        progressBar.progressProperty().bind(task.progressProperty());

        Label label = (Label) r.lookup("#progressLabel");
        label.textProperty().bind(task.messageProperty());
        Stage stage = StageManager.applyStage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(StageManager.customScene(r));
        task.setOnSucceeded(event -> {
            stage.close();
            main.show();
        });
        CompletableFuture.runAsync(task);
        stage.show();
    }
}
