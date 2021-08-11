
package rest.view.component;

import static rest.util.CommonUtil.REST_ENTITY_NAME_PAT;

import com.jfoenix.controls.JFXListView;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCombination;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import rest.controller.plugin.eximport.json.JsonPort;
import rest.controller.plugin.stages.StageManager;
import rest.persitence.PersistEngine;
import rest.persitence.model.RestEntity;
import rest.util.CommonUtil;
import rest.util.ViewUtil;
import rest.view.BootFrame;
import rest.view.basic.JFXTextInputDialog;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.download.DownloadView;
import rest.view.tabs.history.Recoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * menu item for application
 */
public class MenuFrame extends MenuBar {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuFrame.class);

    private MenuItem newItem = new MenuItem("new");
    private MenuItem open = new MenuItem("open");
    private MenuItem save = new MenuItem("save");
    private MenuItem saveAll = new MenuItem("save all");
    private MenuItem variable = new MenuItem("edit variable");

    private MenuItem importJson = new MenuItem("from json");
    private MenuItem importExcel = new MenuItem("from excel");




    private MenuItem download = new MenuItem("download view");
    private MenuItem about = new MenuItem("about");
    private MenuItem operateHisto = new MenuItem("operate history");

    private MenuItem bs = new MenuItem("bootstrap");
    private MenuItem def = new MenuItem("default");
    private MenuItem darcula = new MenuItem("dark");

    public MenuFrame() {
        Menu file = getTopMenu("file");
        open.setAccelerator(KeyCombination.valueOf("ctrl+o"));
        newItem.setAccelerator(KeyCombination.valueOf("ctrl+n"));
        save.setAccelerator(KeyCombination.valueOf("ctrl+s"));
        saveAll.setAccelerator(KeyCombination.valueOf("ctrl+shift+s"));

        Menu inport = new Menu("import");
        inport.getItems().addAll(importJson,importExcel);
        file.getItems().addAll(open, newItem, save, saveAll,inport, new SeparatorMenuItem(), variable);

        Menu help = getTopMenu("help");
        help.getItems().add(operateHisto);
        help.getItems().add(download);
        help.getItems().add(about);
        Menu themes = new Menu("themes");
        help.getItems().add(themes);

        themes.getItems().addAll(bs, def, darcula);

        getMenus().addAll(file, help);
        registerEvent();
    }

    private static Menu getTopMenu(String text) {
        Menu menu = new Menu(text);
        menu.setStyle("-fx-font-size:12");
        return menu;
    }

    private void registerEvent() {
        newItem.setOnAction(event -> {
            JFXTextInputDialog dialog = new JFXTextInputDialog();
            ButtonType ok = ViewUtil.customDialog(dialog);
            dialog.setTitle("new entity name");

            List<RestEntity> entities = PersistEngine.instance().loadEntities();
            dialog.getDialogPane().lookupButton(ok).setDisable(true);
            dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if ("".equals(CommonUtil.noNullTrimStr(newValue)) || !newValue.matches(REST_ENTITY_NAME_PAT)
                    || entities.stream().anyMatch(r -> r.getName().equals(CommonUtil.noNullTrimStr(newValue)))) {
                    dialog.getDialogPane().lookupButton(ok).setDisable(true);
                } else {
                    dialog.getDialogPane().lookupButton(ok).setDisable(false);
                }
            });
            dialog.showAndWait().ifPresent(s -> {
                RestEntity restEntity = PersistEngine.instance().newEntityWithName(s);
                restEntity.setOpened(true);
                restEntity.setCurrent(true);
                StageManager.primaryFrame().loadEntity(restEntity);
            });
        });
        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Dialog<RestEntity> dialog = new Dialog<>();
                ButtonType ok = ViewUtil.customDialog(dialog);
                dialog.getDialogPane().setMinWidth(250);
                dialog.setTitle("entity list");
                List<RestEntity> entities = PersistEngine.instance().loadEntities();
                JFXListView<RestEntity> listView = new JFXListView<>();
                listView.setCellFactory(new Callback<ListView<RestEntity>, ListCell<RestEntity>>() {
                    @Override
                    public ListCell<RestEntity> call(ListView<RestEntity> param) {
                        return new ListCell<RestEntity>() {
                            @Override
                            protected void updateItem(RestEntity item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    setText(entities.indexOf(item) + "." + item.getName() + "[" + item.getBatch().size()
                                        + " items]");
                                    setGraphic(null);
                                }
                            }
                        };
                    }
                });
                listView.setItems(FXCollections.observableArrayList(entities));
                dialog.getDialogPane().setContent(listView);
                dialog.setResultConverter(param -> param == ok ? listView.getSelectionModel().getSelectedItem() : null);
                dialog.showAndWait().ifPresent(restEntity -> StageManager.primaryFrame().loadEntity(restEntity));
            }
        });
        variable
            .setOnAction(event -> ViewUtil.variableDialog("global variable", PersistEngine.instance().loadVariable())
                .showAndWait()
                .ifPresent(s -> PersistEngine.instance().persistVariable(s)));
        save.setOnAction(event -> {
            LOGGER.info("start save current rest entity");
            Tab table = StageManager.primaryFrame().getSelectionModel().getSelectedItem();
            if (table instanceof BatchRequestTable) {
                ((BatchRequestTable) table).persist();
            }
        });
        saveAll.setOnAction(event -> {
            LOGGER.info("save all rest entities start");
            StageManager.primaryFrame()
                .getTabs()
                .stream()
                .filter(v -> v instanceof BatchRequestTable)
                .map(v -> (BatchRequestTable) v)
                .forEach(BatchRequestTable::persist);
        });
        importJson.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                JsonPort.inport();
            }
        });


        operateHisto.setOnAction(event -> Recoder.inst().show());
        download.setOnAction(event -> DownloadView.instance().show());
        about.setOnAction(event -> {
            Dialog dialog = new Dialog();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.initOwner(StageManager.mainStage());
            WebView webView = new WebView();
            webView.getEngine().load(BootFrame.class.getResource("/intro.html").toExternalForm());
            dialog.getDialogPane().setContent(webView);
            dialog.show();
        });

        bs.setOnAction(event -> StageManager.changeTheme("/theme/bs.css"));
        def.setOnAction(event -> StageManager.changeTheme("/theme/default.css"));
        darcula.setOnAction(event -> StageManager.changeTheme("/theme/dark.css"));
    }

}
