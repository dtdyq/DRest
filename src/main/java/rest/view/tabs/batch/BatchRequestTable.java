


package rest.view.tabs.batch;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import rest.controller.plugin.clipboard.ClipBoardManager;
import rest.controller.plugin.eximport.json.JsonPort;
import rest.controller.plugin.stages.StageManager;
import rest.persitence.PersistEngine;
import rest.persitence.model.RestEntity;
import rest.persitence.model.RestItem;
import rest.util.CommonUtil;
import rest.util.ModelUtil;
import rest.util.ViewUtil;
import rest.view.model.EntityBinding;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.vassal.columns.BodyColumn;
import rest.view.tabs.batch.vassal.columns.HeadFormColumn;
import rest.view.tabs.batch.vassal.columns.MethodColumn;
import rest.view.tabs.batch.vassal.columns.NameColumn;
import rest.view.tabs.batch.vassal.columns.ReqNoColumn;
import rest.view.tabs.batch.vassal.columns.RespHeadColumn;
import rest.view.tabs.batch.vassal.columns.ResponseColumn;
import rest.view.tabs.batch.vassal.columns.RunColumn;
import rest.view.tabs.batch.vassal.columns.StatusColumn;
import rest.view.tabs.batch.vassal.columns.TimeColumn;
import rest.view.tabs.batch.vassal.columns.UrlColumn;
import rest.view.tabs.batch.vassal.ctxmenus.BasicAuthMenu;
import rest.view.tabs.batch.vassal.ctxmenus.ProxyMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RestAddMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RestDelMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RestDupMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RestExportMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RestInsertMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RestLogMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RestRenameMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RunParalMenu;
import rest.view.tabs.batch.vassal.ctxmenus.RunSeqMenu;
import rest.view.tabs.batch.vassal.ctxmenus.SslVerifyMenu;
import rest.view.tabs.request.HttpFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * rest entity display
 */
public class BatchRequestTable extends Tab {
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchRequestTable.class);

    private final PersistEngine persistEngine = PersistEngine.instance();

    private TableView<RequestBinding> table = new TableView<>();

    private Map<RequestBinding, HttpFrame> frames = new HashMap<>();

    private EntityBinding entityBinding;

    public BatchRequestTable(EntityBinding entity) {
        this.entityBinding = entity;
        draw();
        register();
        render();
    }

    private void register() {
        registerTab();
        registerMenu();
        registerTable();
    }

    private void registerTab() {
        setOnCloseRequest(event -> {
            persist();
            frames.forEach((k, v) -> StageManager.primaryFrame().getTabs().remove(v));
        });
        setOnSelectionChanged(event -> entityBinding.setCurrent(isSelected()));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem variable = new MenuItem("variable");
        variable.setOnAction(event -> {
            ViewUtil.variableDialog(entityBinding.getName() + " variable", entityBinding.getVariable())
                .showAndWait()
                .ifPresent(s -> entityBinding.setVariable(s));
        });
        Menu export = new Menu("export");
        MenuItem toJson = new MenuItem("to json");
        MenuItem toExcel = new MenuItem("to excel");
        toJson.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                JsonPort.export(entityBinding);
            }
        });
        toExcel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        export.getItems().addAll(toExcel, toJson);
        MenuItem delete = new MenuItem("delete");
        delete.setOnAction(event -> {
            Dialog<Boolean> dialog = new Dialog<>();
            ButtonType ok = ViewUtil.customDialog(dialog);
            dialog.getDialogPane().setContent(new Label("confirm to delete it?"));
            dialog.setTitle("confirm");
            dialog.setResultConverter(param -> param == ok);
            dialog.showAndWait().ifPresent(aBoolean -> {
                if (aBoolean) {
                    PersistEngine.instance().deleteEntityByName(entityBinding.getName());
                    StageManager.primaryFrame().getTabs().remove(BatchRequestTable.this);
                }
            });
        });

        contextMenu.getItems().addAll(variable, export, delete);
        setContextMenu(contextMenu);
    }

    private void draw() {
        table.setTableMenuButtonVisible(true);
        table.setPlaceholder(new Label(""));
        setContent(table);
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        NameColumn name = new NameColumn(this);
        ReqNoColumn reqNo = new ReqNoColumn(this);
        MethodColumn method = new MethodColumn(this);
        UrlColumn url = new UrlColumn(this);
        HeadFormColumn header = new HeadFormColumn(this);
        BodyColumn body = new BodyColumn(this);
        RunColumn run = new RunColumn(this);
        StatusColumn code = new StatusColumn(this);
        TimeColumn time = new TimeColumn(this);
        ResponseColumn response = new ResponseColumn(this);
        RespHeadColumn respHeader = new RespHeadColumn(this);

        table.getColumns().addAll(reqNo, name, method, url, header, body, run, code, time, respHeader, response);

        reqNo.prefWidthProperty().bind(table.widthProperty().multiply(0.03));
        name.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
        method.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        url.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        header.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
        run.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        body.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
        code.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        time.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
        respHeader.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
        response.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
    }

    public EntityBinding getEntityBinding() {
        return entityBinding;
    }

    public TableView<RequestBinding> getTable() {
        return this.table;
    }

    private void registerMenu() {
        RestRenameMenu rename = new RestRenameMenu(this);
        RestAddMenu add = new RestAddMenu(this);
        RestInsertMenu insert = new RestInsertMenu(this);
        RestDupMenu dup = new RestDupMenu(this);
        Menu runMenu = new Menu("run selected");
        RunParalMenu parallel = new RunParalMenu(this);
        RunSeqMenu sequential = new RunSeqMenu(this);
        runMenu.getItems().addAll(sequential, parallel);
        RestDelMenu delete = new RestDelMenu(this);
        BasicAuthMenu basicAuth = new BasicAuthMenu(this);
        ProxyMenu proxy = new ProxyMenu(this);
        SslVerifyMenu ssl = new SslVerifyMenu(this);

        RestLogMenu log = new RestLogMenu(this);

        RestExportMenu exportMenu = new RestExportMenu(this);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems()
            .addAll(rename, add, insert, dup, delete, new SeparatorMenuItem(), runMenu, new SeparatorMenuItem(),
                basicAuth, proxy, ssl, new SeparatorMenuItem(), log,exportMenu);

        table.setOnContextMenuRequested(event -> {
            contextMenu.getItems().forEach(menu -> menu.setDisable(false));
            ObservableList<RequestBinding> items = table.getSelectionModel().getSelectedItems();
            if (items.isEmpty()) {
                rename.setDisable(true);
                dup.setDisable(true);
                delete.setDisable(true);
                runMenu.setDisable(true);
                exportMenu.setDisable(true);
            } else if (items.size() > 1) {
                exportMenu.setDisable(true);
                rename.setDisable(true);
                dup.setDisable(true);
                basicAuth.setDisable(true);
                proxy.setDisable(true);
                ssl.setDisable(true);
            }
        });
        table.setContextMenu(contextMenu);
    }

    @SuppressWarnings("unchecked")
    private void registerTable() {
        table.setRowFactory(tv1 -> {
            TableRow<RequestBinding> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    RequestBinding t = table.getItems().remove(draggedIndex);
                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = table.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }
                    table.getItems().add(dropIndex, t);
                    event.setDropCompleted(true);
                    table.getSelectionModel().select(dropIndex);
                    event.consume();

                }
            });
            return row;
        });
        table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                RequestBinding binding = table.getSelectionModel().getSelectedItem();
                if (binding == null) {
                    return;
                }
                String name = entityBinding.getName() + "/" + binding.getName();
                Tab exist = StageManager.primaryFrame()
                    .getTabs()
                    .stream()
                    .filter(tab -> tab.getText().equals(name))
                    .findFirst()
                    .orElse(null);
                if (exist != null) {
                    StageManager.primaryFrame().getSelectionModel().select(exist);
                } else {
                    binding.setOpen(true);
                    binding.setCurrent(true);
                    HttpFrame frame = new HttpFrame(entityBinding.nameProperty(), binding);
                    StageManager.primaryFrame().getTabs().add(frame);
                    if (binding.isCurrent()) {
                        StageManager.primaryFrame().getSelectionModel().select(frame);
                    }
                    frames.put(binding, frame);
                }
            }
        });
        table.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                ObservableList<RequestBinding> items = table.getSelectionModel().getSelectedItems();
                if (items == null) {
                    items = FXCollections.observableArrayList();
                }
                ClipBoardManager.instance().remove(SERIALIZED_MIME_TYPE);
                ClipBoardManager.instance().put(SERIALIZED_MIME_TYPE, items);
            }
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                Object obj = ClipBoardManager.instance().get(SERIALIZED_MIME_TYPE);
                if (obj instanceof ObservableList) {
                    ObservableList<RequestBinding> items = (ObservableList<RequestBinding>) obj;
                    ObservableList<RequestBinding> copy = copy(items);
                    entityBinding.addRequests(copy);
                }
            }
        });

    }

    private ObservableList<RequestBinding> copy(ObservableList<RequestBinding> items) {
        ObservableList<RequestBinding> ret = FXCollections.observableArrayList();
        List<String> names =
            entityBinding.getRequests().stream().map(RequestBinding::getName).collect(Collectors.toList());
        items.forEach(binding -> {
            RequestBinding temp = new RequestBinding(binding);
            String name = CommonUtil.uniqueItemName(names, binding.getName());
            temp.setName(name);
            names.add(name);
            ret.add(temp);
        });
        return ret;
    }

    public void render() {
        setGraphic(ViewUtil.genImageViewForTab(CommonUtil.ENTITY_TAB_GRAPHIC_ICON));
        textProperty().bind(entityBinding.nameProperty());
        LOGGER.info("render data:{}:", this.entityBinding);
        table.setItems(entityBinding.getRequests());
        Platform.runLater(this::renderFrame);
    }

    private void renderFrame() {
        entityBinding.getRequests().forEach(binding -> {
            if (binding.isOpen()) {
                HttpFrame frame = new HttpFrame(entityBinding.nameProperty(), binding);
                StageManager.primaryFrame().getTabs().add(frame);
                if (binding.isCurrent()) {
                    StageManager.primaryFrame().getSelectionModel().select(frame);
                }
                frames.put(binding, frame);
            }
        });
    }

    public void persist() {
        List<RestItem> batch = table.getItems().stream().map(ModelUtil::requestBinding2RestItem).collect(Collectors.toList());
        RestEntity entity = new RestEntity();
        entity.setOpened(entityBinding.isOpen());
        entity.setName(entityBinding.getName());
        entity.setCurrent(entityBinding.isCurrent());
        entity.setBatch(batch);
        entity.variable(entityBinding.getVariable());
        persistEngine.persistEntities(Collections.singletonList(entity));
    }

    public void delItems(List<String> bindings) {
        bindings.forEach(binding -> {
            entityBinding.removeRequestByName(binding);
            StageManager.primaryFrame()
                .getTabs()
                .removeIf(tab -> tab.getText().equals(entityBinding.getName() + "/" + binding));
        });
    }
}