
package rest.view.tabs.batch.vassal.columns;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * display response status
 */
public class StatusColumn extends SuperColumn {
    private TimeColumn.Oper operNext = TimeColumn.Oper.DEFAULT;

    public StatusColumn(BatchRequestTable table) {
        super(table);
        Label title = new Label("code");
        setGraphic(title);
        Map<RequestBinding, Integer> mirror = new HashMap<>();
        title.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (operNext == TimeColumn.Oper.DEFAULT) {
                    ObservableList<RequestBinding> tmp = table.getTable().getItems();
                    for (int i = 0; i < tmp.size(); i++) {
                        mirror.put(tmp.get(i), i);
                    }
                    table.getTable().getItems().sort(Comparator.comparing(RequestBinding::getCode));
                    operNext = TimeColumn.Oper.ASCENDING;
                } else if (operNext == TimeColumn.Oper.ASCENDING) {
                    ObservableList<RequestBinding> tmp = table.getTable().getItems();
                    for (int i = 0; i < tmp.size(); i++) {
                        if (!mirror.containsKey(tmp.get(i))) {
                            mirror.put(tmp.get(i), i);
                        }
                    }
                    tmp.sort(Comparator.comparingInt(o -> mirror.getOrDefault(o, tmp.indexOf(o))));
                    operNext = TimeColumn.Oper.DEFAULT;
                }
                event.consume();
            }
        });
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new CodeCellHandler(table);
    }

    private class CodeCellHandler implements CellHandler {
        private final BatchRequestTable table;

        CodeCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        void renderColor(Label label) {
            String newValue = label.getText();
            if (newValue.startsWith("2")) {
                label.setStyle("-fx-text-fill: green");
            } else if (newValue.startsWith("1")) {
                label.setStyle("-fx-text-fill: cyan");
            } else if (newValue.startsWith("3")) {
                label.setStyle("-fx-text-fill: blue");
            } else if (newValue.startsWith("4")) {
                label.setStyle("-fx-text-fill: orangered");
            } else if (newValue.startsWith("5")) {
                label.setStyle("-fx-text-fill: red");
            } else {
                label.setStyle("-fx-text-fill: black");
            }
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            Label label = new Label();
            RequestBinding binding = table.getTable().getItems().get(cell.getIndex());
            label.textProperty().bindBidirectional(binding.codeProperty());
            renderColor(label);
            label.textProperty().addListener((observable, oldValue, newValue) -> renderColor(label));
            cell.setGraphic(label);
        }
    }
}
