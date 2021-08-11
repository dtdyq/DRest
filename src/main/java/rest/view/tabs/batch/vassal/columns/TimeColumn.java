
package rest.view.tabs.batch.vassal.columns;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * display request cost time
 */
public class TimeColumn extends SuperColumn {

    private Comparator<RequestBinding> comparator = (o1, o2) -> {
        if (o1.getTime().equals("---")) {
            return -1;
        }
        if (o2.getTime().equals("---")) {
            return 1;
        }
        if (o1.getTime().equals("---") && o2.getTime().equals("---")) {
            return 0;
        }
        return Long.compare(Long.valueOf(o1.getTime()), Long.valueOf(o2.getTime()));
    };

    private Oper operNext = Oper.DEFAULT;

    public TimeColumn(BatchRequestTable table) {
        super(table);
        Label title = new Label("cost(ms)");
        setGraphic(title);
        Map<RequestBinding, Integer> mirror = new HashMap<>();
        title.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (operNext == Oper.DEFAULT) {
                    ObservableList<RequestBinding> tmp = table.getTable().getItems();
                    for (int i = 0; i < tmp.size(); i++) {
                        mirror.put(tmp.get(i), i);
                    }
                    table.getTable().getItems().sort(comparator);
                    operNext = Oper.ASCENDING;
                } else if (operNext == Oper.ASCENDING) {
                    table.getTable().getItems().sort(comparator.reversed());
                    operNext = Oper.DESCENDING;
                } else if (operNext == Oper.DESCENDING) {
                    ObservableList<RequestBinding> tmp = table.getTable().getItems();
                    for (int i = 0; i < tmp.size(); i++) {
                        if (!mirror.containsKey(tmp.get(i))) {
                            mirror.put(tmp.get(i), i);
                        }
                    }
                    tmp.sort(Comparator.comparingInt(o -> mirror.getOrDefault(o, tmp.indexOf(o))));
                    operNext = Oper.DEFAULT;
                }
                event.consume();
            }
        });
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new TimeCellHandler(table);
    }

    enum Oper {
        ASCENDING, DESCENDING, DEFAULT
    }

    private class TimeCellHandler implements CellHandler {
        private final BatchRequestTable table;

        TimeCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            Label label = new Label();
            RequestBinding binding = table.getTable().getItems().get(cell.getIndex());

            label.textProperty().bindBidirectional(binding.timeProperty());
            renderColor(label);
            label.textProperty().addListener((observable, oldValue, newValue) -> renderColor(label));
            cell.setGraphic(label);

        }

        void renderColor(Label label) {
            String newValue = label.getText();
            if ("---".equals(newValue)) {
                label.setTextFill(Color.BLACK);
            } else {
                int i = Integer.parseInt(newValue);
                if (i == 0) {
                    label.setText("---");
                } else if (i <= 200) {
                    label.setStyle("-fx-text-fill: green");
                } else if (i <= 500) {
                    label.setStyle("-fx-text-fill: orange");
                } else if (i <= 1000) {
                    label.setStyle("-fx-text-fill: orangered");
                } else {
                    label.setStyle("-fx-text-fill: red");
                }
            }
        }
    }
}
