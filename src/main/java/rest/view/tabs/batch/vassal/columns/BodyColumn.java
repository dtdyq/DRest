
package rest.view.tabs.batch.vassal.columns;

import static rest.util.ViewUtil.createCellBtn;

import com.jfoenix.controls.JFXTextArea;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import rest.controller.plugin.stages.StageManager;
import rest.util.CommonUtil;
import rest.util.ViewUtil;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;
import rest.view.tabs.history.Recoder;

/**
 * column for showing request body data
 */
public class BodyColumn extends SuperColumn {
    public BodyColumn(BatchRequestTable table) {
        super(table);
        setText("body");
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new BodyCellHandler(table);
    }

    private class BodyCellHandler implements CellHandler {

        private final BatchRequestTable table;

        public BodyCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            Button button = createCellBtn("  body  ");
            button.setOnAction(event -> {
                JFXTextArea area = new JFXTextArea();
                RequestBinding binding = table.getTable().getItems().get(cell.getIndex());
                area.setText(binding.getBody());
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("request data");
                ButtonType ok = ViewUtil.customDialog(dialog);
                dialog.getDialogPane().setContent(area);
                dialog.setResultConverter(param -> {
                    if (param == ok) {
                        return area.getText();
                    }
                    return null;
                });
                dialog.getDialogPane().setMinWidth(StageManager.mainStage().getWidth() * 0.6);
                dialog.getDialogPane().setMinHeight(StageManager.mainStage().getHeight() * 0.6);
                dialog.setX(StageManager.mainStage().getX() + StageManager.mainStage().getWidth() * 0.2);
                dialog.setY(StageManager.mainStage().getY() + StageManager.mainStage().getHeight() * 0.1);
                dialog.showAndWait().ifPresent(s -> {
                    if (!s.equals(binding.getBody())) {
                        binding.setBody(s);
                        record(binding, s);
                    }
                });
            });
            cell.setGraphic(button);
        }

        private void record(RequestBinding binding, String s) {
            Recoder.inst()
                .info(CommonUtil.record("set rest item body data", table.getEntityBinding().getName(),
                    binding.getName(), s), s);
        }
    }
}
