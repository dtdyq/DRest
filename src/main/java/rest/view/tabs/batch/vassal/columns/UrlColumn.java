

package rest.view.tabs.batch.vassal.columns;

import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import rest.util.ViewUtil;
import rest.view.basic.JFXTextInputDialog;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;

import java.util.List;

/**
 * column for edit request url
 */
public class UrlColumn extends SuperColumn {
    public UrlColumn(BatchRequestTable table) {
        super(table);
        setText("url");
    }

    @Override
    protected void registerMenus(List<MenuItem> items, BatchRequestTable table) {
        MenuItem menuItem = new MenuItem("batch edit");
        menuItem.setOnAction(event -> {
            JFXTextInputDialog dialog = new JFXTextInputDialog();
            ViewUtil.customDialog(dialog);
            dialog.setTitle("batch edit url");
            dialog.showAndWait().ifPresent(s -> table.getTable().getItems().forEach(binding -> binding.setUrl(s)));
        });
        items.add(menuItem);
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new UrlCellHandler(table);
    }

    private class UrlCellHandler implements CellHandler {
        private final BatchRequestTable table;

        UrlCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            JFXTextField textField = new JFXTextField();
            RequestBinding binding = table.getTable().getItems().get(cell.getIndex());
            cell.setGraphic(textField);
            textField.textProperty().bindBidirectional(binding.urlProperty());
            }
    }
}
