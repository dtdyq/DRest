

package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

/**
 * run selected item parallel
 */
public class RunParalMenu extends SuperMenuItem {
    public RunParalMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("parallel");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        for (RequestBinding item : bindings) {
            item.setRunEvent(new RequestBinding.RunEvent(requestTable.getTable().getItems().indexOf(item), false));
            item.setRunEvent(new RequestBinding.RunEvent(-1, false));
        }
    }
}
