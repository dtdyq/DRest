

package rest.view.tabs.batch.vassal.columns;

import static rest.util.ViewUtil.createCellBtn;

import com.jfoenix.controls.JFXTextArea;

import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import rest.controller.plugin.stages.StageManager;
import rest.util.ViewUtil;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;

/**
 * display response body
 */
public class ResponseColumn extends SuperColumn {

    public ResponseColumn(BatchRequestTable table) {
        super(table);
        setText("result");
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new BodyCellHandler(table);
    }

    private class BodyCellHandler implements CellHandler {

        private final BatchRequestTable table;

        BodyCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            Button button = createCellBtn("  resp  ");
            button.setOnAction(event -> {
                JFXTextArea area = new JFXTextArea();
                RequestBinding binding = table.getTable().getItems().get(cell.getIndex());
                area.setText(binding.getResponse());
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("response data");
                ViewUtil.customConfirmDialog(dialog);
                dialog.getDialogPane().setContent(area);
                area.setEditable(false);
                dialog.getDialogPane().setMinWidth(StageManager.mainStage().getWidth() * 0.6);
                dialog.getDialogPane().setMinHeight(StageManager.mainStage().getHeight() * 0.6);
                dialog.setX(StageManager.mainStage().getX() + StageManager.mainStage().getWidth() * 0.2);
                dialog.setY(StageManager.mainStage().getY() + StageManager.mainStage().getHeight() * 0.1);
                dialog.show();
            });
            cell.setGraphic(button);
        }
    }
}
