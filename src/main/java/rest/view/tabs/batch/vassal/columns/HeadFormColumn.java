
package rest.view.tabs.batch.vassal.columns;

import static rest.util.ViewUtil.createCellBtn;
import static rest.util.ViewUtil.customDialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import rest.controller.plugin.stages.StageManager;
import rest.util.CommonUtil;
import rest.view.basic.EditableKeyValTable;
import rest.view.basic.KeyValTable;
import rest.view.model.KeyValue;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;
import rest.view.tabs.history.Recoder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * head form edit cloumn,support edit head/form,and batch edit head/form
 */
public class HeadFormColumn extends SuperColumn {

    public HeadFormColumn(BatchRequestTable table) {
        super(table);
        setText("head/form");
    }

    private static boolean validHeadField(String s) {
        return s != null && !"".equals(s.trim()) && !"---".equals(s.trim());
    }

    @Override
    protected void registerMenus(List<MenuItem> items, BatchRequestTable table) {
        MenuItem batchEdit = new MenuItem("batch add");
        batchEdit.setOnAction(event -> {
            Dialog<ObservableMap<String, String>> dialog = new Dialog<>();

            ButtonType toHead = new ButtonType("apply to head", ButtonBar.ButtonData.OK_DONE);
            ButtonType toForm = new ButtonType("apply to form", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(toHead, toForm, cancel);
            dialog.getDialogPane().lookupButton(toHead).getStyleClass().addAll("sm", "info");
            dialog.getDialogPane().lookupButton(toForm).getStyleClass().addAll("sm", "info");
            dialog.getDialogPane().lookupButton(cancel).getStyleClass().addAll("sm", "info");
            dialog.setHeaderText(null);
            dialog.setContentText(null);
            dialog.initOwner(StageManager.mainStage());

            EditableKeyValTable keyValTable = new EditableKeyValTable();

            dialog.getDialogPane().setContent(keyValTable);
            dialog.setTitle("batch add head/form");
            dialog.setResultConverter(param -> {
                if (param == toHead) {
                    table.getEntityBinding()
                        .getRequests()
                        .forEach(binding -> binding.setHeader(keyValTable.getItems()));

                    String text = keyValTable.getItems()
                        .stream()
                        .map(kv -> kv.getKey() + ":" + kv.getValue())
                        .collect(Collectors.joining(System.lineSeparator()));
                    Recoder.inst()
                        .info(CommonUtil.record("batch set rest item form data", table.getEntityBinding().getName(),
                            text), text);
                }
                if (param == toForm) {
                    table.getEntityBinding().getRequests().forEach(binding -> binding.setForm(keyValTable.getItems()));
                    String text = keyValTable.getItems()
                        .stream()
                        .map(kv -> kv.getKey() + ":" + kv.getValue())
                        .collect(Collectors.joining(System.lineSeparator()));
                    Recoder.inst()
                        .info(CommonUtil.record("batch set rest item form data", table.getEntityBinding().getName(),
                            text), text);
                }
                return null;
            });
            dialog.showAndWait();

        });
        items.add(batchEdit);
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new HeaderCellHandler(table);
    }

    private Callback<ButtonType, ObservableMap<String, String>> mapConverter(ObservableList<KeyValue> list,
        ButtonType ok) {
        return param -> {
            ObservableMap<String, String> vars = FXCollections.observableHashMap();
            if (param == ok) {
                list.forEach(keyValue -> {
                    String key = keyValue.getKey();
                    String value = keyValue.getValue();
                    if (validHeadField(key) && validHeadField(value)) {
                        vars.put(key.trim(), value.trim());
                    }
                });
                return vars;
            } else {
                return null;
            }
        };
    }

    private class HeaderCellHandler implements CellHandler {

        private final BatchRequestTable table;

        HeaderCellHandler(BatchRequestTable table) {
            this.table = table;
            setText("header/form");
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            Button headForm = createCellBtn(" head/form ");
            headForm.setOnMouseClicked(event -> {
                Dialog<ObservableMap<String, String>> headerDialog = new Dialog<>();
                headerDialog.setTitle("header and form");

                TabPane tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

                RequestBinding binding = table.getTable().getItems().get(cell.getIndex());

                KeyValTable headKV = fillKeyValTable(headerDialog, binding.getHeader());
                Tab headTab = new Tab("header", headKV);
                tabPane.getTabs().add(headTab);

                KeyValTable formKV = fillKeyValTable(headerDialog, binding.getForm());
                Tab formTab = new Tab("form", formKV);
                tabPane.getTabs().add(formTab);

                headerDialog.getDialogPane().setContent(tabPane);

                ButtonType ok = customDialog(headerDialog);
                headerDialog.setResizable(true);
                headerDialog.setResultConverter(param -> {
                    if (param == ok) {
                        binding.setForm(formKV.getItems());
                        binding.setHeader(headKV.getItems());

                        record(binding, headKV, formKV);
                    }
                    return null;
                });
                headerDialog.showAndWait();
            });
            cell.setGraphic(headForm);
        }

        private void record(RequestBinding binding, KeyValTable headKV, KeyValTable formKV) {
            String text = formKV.getItems()
                .stream()
                .map(kv -> kv.getKey() + ":" + kv.getValue())
                .collect(Collectors.joining(System.lineSeparator()));
            Recoder.inst()
                .info(CommonUtil.record("set rest item form data", table.getEntityBinding().getName(),
                    binding.getName(), text), text);

            text = headKV.getItems()
                .stream()
                .map(kv -> kv.getKey() + ":" + kv.getValue())
                .collect(Collectors.joining(System.lineSeparator()));
            Recoder.inst()
                .info(CommonUtil.record("set rest item header data", table.getEntityBinding().getName(),
                    binding.getName(), text), text);
        }

        private KeyValTable fillKeyValTable(Dialog<ObservableMap<String, String>> headerDialog,
            ObservableList<KeyValue> cur) {
            KeyValTable keyValTable = new EditableKeyValTable();
            keyValTable.setItems(cur);
            return keyValTable;
        }
    }
}
