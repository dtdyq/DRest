


package rest.view.tabs.history;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.util.Callback;
import rest.controller.plugin.stages.StageManager;
import rest.util.CommonUtil;
import rest.util.ViewUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * record operation、support rollback. operation type contains record、snapshot、rollback,record is just a msg,rollback is
 * for log roll back operation,snapshot is a record that can be rollback.you can use RestLogMenu to show operation log
 * in a new windows,use RestUndo or ctrl+z on table to rollback.limit is the history records limit,you can only rollback
 * to recently limit number operation
 */
public class Recoder extends Tab {
    private static final Logger LOGGER = LoggerFactory.getLogger(Recoder.class);

    private static final JFXListView<Record> listView = new JFXListView<>();
    private static final Recoder RECODER = new Recoder();

    /**
     * register list view context menu.if the operation item in view list is a snapshot and its mirror is not null,can
     * do rollback
     *
     */
    private Recoder() {
        LOGGER.info("init operation recoder");
        setText("operation history");
        setContent(listView);
        setGraphic(ViewUtil.genImageViewForTab(CommonUtil.HISTORY_TAB_GRAPHIC_ICON));

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(new Callback<ListView<Record>, ListCell<Record>>() {
            @Override
            public ListCell<Record> call(ListView<Record> param) {
                return new ListCell<Record>() {
                    {
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem item = new MenuItem("delete");
                        item.setOnAction(event -> {
                            listView.getItems().removeAll(listView.getSelectionModel().getSelectedItems());
                        });
                        MenuItem detail = new MenuItem("detail");
                        detail.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (listView.getSelectionModel().getSelectedItems().size() == 1) {
                                    Record record = listView.getSelectionModel().getSelectedItems().get(0);
                                    if (record.detail() != null) {
                                        JFXTextArea area = new JFXTextArea();
                                        area.setEditable(false);
                                        area.setText(record.detail());
                                        Dialog<String> dialog = new Dialog<>();
                                        dialog.setTitle("detail");
                                        ViewUtil.customConfirmDialog(dialog);
                                        dialog.getDialogPane().setContent(area);
                                        dialog.getDialogPane().setMinWidth(StageManager.mainStage().getWidth() * 0.6);
                                        dialog.getDialogPane().setMinHeight(StageManager.mainStage().getHeight() * 0.6);
                                        dialog.setX(StageManager.mainStage().getX()
                                            + StageManager.mainStage().getWidth() * 0.2);
                                        dialog.setY(StageManager.mainStage().getY()
                                            + StageManager.mainStage().getHeight() * 0.1);
                                        dialog.showAndWait();
                                    }
                                }
                            }
                        });

                        contextMenu.getItems().addAll(item, detail);
                        setContextMenu(contextMenu);
                    }

                    @Override
                    protected void updateItem(Record item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            LOGGER.info("render new snapshot:{}", item);
                            Label label = new Label();
                            label.setText(item.desc());
                            if (item.level() == 0) {
                                label.setStyle("-fx-text-fill: #4b59ff");
                            } else if (item.level() == 1) {
                                label.setStyle("-fx-text-fill: orange");
                            } else {
                                label.setStyle("-fx-text-fill: #ff4632");
                            }
                            setAlignment(Pos.CENTER_LEFT);
                            setGraphic(label);
                        }
                    }
                };
            }
        });
    }

    public static Recoder inst() {
        return RECODER;
    }

    public void info(String msg) {
        info(msg, null);
    }

    public void info(String msg, String detail) {
        record(0, msg, detail);
    }

    public void warn(String msg) {
        warn(msg, null);
    }

    public void warn(String msg, String detail) {
        record(1, msg, detail);
    }

    public void error(String msg) {
        error(msg, null);
    }

    public void error(String msg, String detail) {
        record(2, msg, detail);
    }

    private void record(int level, String desc, String detail) {
        Record record = new Record(level, new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()) + " : " + desc);
        record.detail(detail);
        listView.getItems().add(0, record);
    }

    public void show() {
        if (StageManager.primaryFrame().getTabs().indexOf(RECODER) == -1) {
            StageManager.primaryFrame().getTabs().add(RECODER);
        }
        StageManager.primaryFrame().getSelectionModel().select(RECODER);
    }
}
