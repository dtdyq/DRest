

package rest.view.tabs.batch.vassal.columns.handler;

import javafx.scene.control.TableCell;
import rest.view.model.RequestBinding;

public interface CellHandler {
    void handle(TableCell<RequestBinding, RequestBinding> cell);
}
