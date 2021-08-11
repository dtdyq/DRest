

package rest.view.tabs.batch.vassal.columns;

import javafx.scene.control.TableCell;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;
import rest.view.model.RequestBinding;

/**
 * display request seq number
 */
public class ReqNoColumn extends SuperColumn {
    public ReqNoColumn(BatchRequestTable table) {
        super(table);
        setText("seq");
    }
    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new ReqNoCellHandler();
    }

    private class ReqNoCellHandler implements CellHandler {
        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            cell.setText(String.valueOf(cell.getIndex()));
        }
    }
}
