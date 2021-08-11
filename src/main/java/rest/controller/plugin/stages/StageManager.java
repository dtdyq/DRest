
package rest.controller.plugin.stages;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import rest.util.ResUtil;
import rest.view.component.MenuFrame;
import rest.view.component.PrimaryFrame;

import java.util.ArrayList;
import java.util.List;

/**
 * manage stages for close
 */
public class StageManager {
    private static final List<Stage> stages = new ArrayList<>();
    private static final List<Scene> SCENES = new ArrayList<>();
    private static String themePath = "/theme/bs.css";
    private static Stage mainStage;
    private static PrimaryFrame primaryFrame;
    private static MenuFrame menuFrame;

    public static Stage applyStage() {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(ResUtil.resPath("/icons/title.png")));
        stages.add(stage);
        return stage;
    }

    public static Scene customScene(Parent node) {
        Scene scene = new Scene(node);
        scene.getStylesheets().add(ResUtil.resPath(themePath));
        SCENES.add(scene);
        return scene;
    }

    public static void releaseAll() {
        stages.forEach(Stage::close);
    }

    public static void changeTheme(String s) {
        themePath = s;
        SCENES.forEach(scene -> {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(ResUtil.resPath(s));
        });
    }

    public static void mainStage(Stage stage) {
        mainStage = stage;
    }

    public static Stage mainStage() {
        return mainStage;
    }

    public static PrimaryFrame primaryFrame() {
        if (primaryFrame == null) {
            primaryFrame = new PrimaryFrame();
        }
        return primaryFrame;
    }

    public static MenuFrame menuFrame() {
        if (menuFrame == null) {
            menuFrame = new MenuFrame();
        }
        return menuFrame;
    }
}
