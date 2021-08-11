

package rest.view.tabs.batch.vassal.columns;

import static rest.util.ViewUtil.createCellBtn;
import static rest.util.ViewUtil.customConfirmDialog;

import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import rest.view.basic.KeyValTable;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;
import rest.view.model.RequestBinding;

/**
 * display response header cookie .etc
 */
public class RespHeadColumn extends SuperColumn {
    public RespHeadColumn(BatchRequestTable table) {
        super(table);
        setText("header");
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new HeaderCellHandler(table);
    }

    private class HeaderCellHandler implements CellHandler {

        private final BatchRequestTable table;

        HeaderCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            Button button = createCellBtn("  header  ");
            button.setOnMouseClicked(event -> {
                Dialog<ObservableMap<String, String>> headerDialog = new Dialog<>();
                headerDialog.setResizable(true);
                headerDialog.setTitle("response header");

                KeyValTable keyValTable = new KeyValTable();

                RequestBinding binding = table.getTable().getItems().get(cell.getIndex());
                keyValTable.setItems(binding.getRespHeader());
                customConfirmDialog(headerDialog);
                headerDialog.getDialogPane().setContent(keyValTable);
                headerDialog.show();
            });
            cell.setGraphic(button);
        }
    }
}
