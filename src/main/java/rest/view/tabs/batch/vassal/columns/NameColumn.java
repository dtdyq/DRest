

package rest.view.tabs.batch.vassal.columns;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;
import rest.view.model.RequestBinding;

/**
 * display rest item name
 */
public class NameColumn extends SuperColumn {
    public NameColumn(BatchRequestTable table) {
        super(table);
        setText("name");
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new SelectedCellHandler(table);
    }

    private class SelectedCellHandler implements CellHandler {
        private final BatchRequestTable table;

        SelectedCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            RequestBinding binding = table.getTable().getItems().get(cell.getIndex());
            Label label = new Label(binding.getName());
            cell.setGraphic(label);
            label.textProperty().bindBidirectional(binding.nameProperty());
        }
    }

}
