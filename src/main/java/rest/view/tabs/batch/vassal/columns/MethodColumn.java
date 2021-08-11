
package rest.view.tabs.batch.vassal.columns;

import com.jfoenix.controls.JFXComboBox;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import rest.util.ViewUtil;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;

import java.util.List;

/**
 * request method column
 */
public class MethodColumn extends SuperColumn {
    public MethodColumn(BatchRequestTable table) {
        super(table);
        setText("method");
    }

    @Override
    protected void registerMenus(List<MenuItem> items, BatchRequestTable table) {
        MenuItem menuItem = new MenuItem("batch set");
        menuItem.setOnAction(event -> {
            String[] mes = new String[] {"get", "post", "put", "del", "patch"};
            ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(mes[0], mes);
            choiceDialog.setTitle("set method for all");
            choiceDialog.getDialogPane().getButtonTypes().clear();
            ViewUtil.customDialog(choiceDialog);
            choiceDialog.showAndWait()
                .ifPresent(s -> table.getTable().getItems().forEach(binding -> binding.setMethod(s)));
        });
        items.add(menuItem);
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new MethodCellHandler(table);
    }

    private class MethodCellHandler implements CellHandler {
        private final BatchRequestTable table;

        MethodCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            JFXComboBox<String> choiceBox =
                new JFXComboBox<>(FXCollections.observableArrayList("get", "post", "put", "del", "patch"));
            choiceBox.getStyleClass().add("sm");
            RequestBinding binding = table.getTable().getItems().get(cell.getIndex());
            choiceBox.valueProperty().bindBidirectional(binding.methodProperty());
            cell.setGraphic(choiceBox);
        }

    }
}
