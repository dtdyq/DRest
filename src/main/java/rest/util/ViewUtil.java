package rest.util;

import com.jfoenix.controls.JFXButton;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import rest.controller.plugin.stages.StageManager;
import rest.view.basic.EditableKeyValTable;
import rest.view.model.KeyValue;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * construct basic widget for app
 */
public final class ViewUtil {
    public static ImageView genImageViewForMenu(String name) {
        Image image = new Image(Objects.requireNonNull(ResUtil.resInputStream(name)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(17);
        imageView.setFitHeight(17);
        return imageView;
    }

    public static ImageView genImageViewForTab(String name) {
        Image image = new Image(Objects.requireNonNull(ResUtil.resInputStream(name)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(14);
        imageView.setFitHeight(14);
        return imageView;
    }

    /**
     * generate a default cell for table column
     *
     * @param cellHandler cell handler
     * @return call back
     */
    public static Callback<TableColumn<RequestBinding, RequestBinding>, TableCell<RequestBinding, RequestBinding>>
        createDefaultCell(CellHandler cellHandler) {
        return new Callback<TableColumn<RequestBinding, RequestBinding>, TableCell<RequestBinding, RequestBinding>>() {
            @Override
            public TableCell<RequestBinding, RequestBinding> call(TableColumn<RequestBinding, RequestBinding> param) {
                return new TableCell<RequestBinding, RequestBinding>() {
                    @Override
                    protected void updateItem(RequestBinding item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setAlignment(Pos.CENTER);
                            cellHandler.handle(this);
                            setPadding(new Insets(0, 0, 0, 0));
                        }
                    }
                };
            }
        };
    }

    /**
     * generate button for table cell
     *
     * @param text btn text
     * @return btn
     */
    public static Button createCellBtn(String text) {
        JFXButton button = new JFXButton(text);
        button.setFont(Font.font(32));
        button.setPadding(new Insets(0, 0, 0, 0));
        return button;
    }

    /**
     * generate split button for table cell
     *
     * @param text btn text
     * @return btn
     */
    public static SplitMenuButton createCellSplitBtn(String text) {
        SplitMenuButton button = new SplitMenuButton();
        button.setText(text);
        return button;
    }

    /**
     * generate split menu button for table cell
     *
     * @param text btn text
     * @return btn
     */
    public static MenuItem createCellMenuItemBtn(String text) {
        MenuItem button = new MenuItem();
        button.setText(text);
        return button;
    }

    /**
     * custom dialog:resize btn/add owner/clear the unused black like header text
     *
     * @param dialog dialog
     * @return ok btn
     */
    public static ButtonType customDialog(Dialog dialog) {
        ButtonType ok = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);
        dialog.getDialogPane().lookupButton(ok).getStyleClass().addAll("sm", "info");
        dialog.getDialogPane().lookupButton(cancel).getStyleClass().addAll("sm", "info");
        dialog.setHeaderText(null);
        dialog.setContentText(null);
        dialog.setGraphic(null);
        dialog.initOwner(StageManager.mainStage());
        return ok;
    }

    public static ButtonType customConfirmDialog(Dialog dialog) {
        ButtonType ok = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ok);
        dialog.getDialogPane().lookupButton(ok).getStyleClass().addAll("sm", "info");
        dialog.setHeaderText(null);
        dialog.setContentText(null);
        dialog.initOwner(StageManager.mainStage());
        return ok;
    }

    public static Label customLabel(String title) {
        Label label = new Label(title);
        label.setFont(Font.font(14));
        return label;
    }

    public static HBox customHBox() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(0, 5, 0, 5));
        return hBox;
    }

    /**
     * customed tooltip for better use
     *
     * @return tooltip
     */
    public static Tooltip customTooltip() {
        Tooltip tooltip = new Tooltip();
        try {
            Class tipClass = tooltip.getClass();
            Field f = tipClass.getDeclaredField("BEHAVIOR");
            f.setAccessible(true);
            Class behavior = Class.forName("javafx.scene.control.Tooltip$TooltipBehavior");
            Constructor constructor =
                behavior.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            f.set(behavior, constructor.newInstance(new Duration(0), new Duration(1000), new Duration(0), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tooltip;
    }

    /**
     * a dialog support auto close
     *
     * @param msg msg
     */
    public static void alert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(null);
        alert.setTitle(null);
        alert.initOwner(StageManager.mainStage());
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.getButtonTypes().clear();
        Label label = ViewUtil.customLabel(msg);
        label.setTextFill(Color.RED);
        alert.getDialogPane().setContent(label);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event1 -> {
            alert.setResult(ButtonType.CANCEL);
            alert.hide();
        }));
        timeline.setCycleCount(1);
        timeline.play();
        alert.show();
    }

    public static Node genRunView(String s) {
        Image image = new Image(Objects.requireNonNull(ResUtil.resInputStream(s)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(14);
        imageView.setFitHeight(14);
        return imageView;
    }

    public static Dialog<Map<String, String>> variableDialog(String title, Map<String, String> toBeDisplay) {
        Dialog<Map<String, String>> varLog = new Dialog<>();
        varLog.setTitle(title);
        ButtonType ok = ViewUtil.customDialog(varLog);
        EditableKeyValTable keyValTable = new EditableKeyValTable();
        ObservableList<KeyValue> list = FXCollections.observableArrayList(toBeDisplay.entrySet()
            .stream()
            .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList()));
        keyValTable.setItems(list);
        varLog.getDialogPane().setContent(keyValTable);

        varLog.setResultConverter(param -> {
            Map<String, String> vars = new HashMap<>();
            if (param == ok) {
                keyValTable.getItems().forEach(keyValue -> vars.put(keyValue.getKey(), keyValue.getValue()));
                return vars;
            }
            return null;
        });
        return varLog;
    }
}
