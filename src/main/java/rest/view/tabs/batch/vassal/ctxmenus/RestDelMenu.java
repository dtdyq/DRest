
package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCombination;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

import java.util.stream.Collectors;

/**
 * delete selected rest items
 */
public class RestDelMenu extends SuperMenuItem {
    public RestDelMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setAccelerator(KeyCombination.valueOf("ctrl+d"));
        setText("delete");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        if (bindings.isEmpty()) {
            return;
        }
        requestTable.delItems(bindings.stream().map(RequestBinding::getName).collect(Collectors.toList()));

    }
}
