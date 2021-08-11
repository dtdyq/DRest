
package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

/**
 * run selected items sequential
 */
public class RunSeqMenu extends SuperMenuItem {
    public RunSeqMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("sequential");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        for (RequestBinding item : bindings) {
            item.setRunEvent(new RequestBinding.RunEvent(requestTable.getTable().getItems().indexOf(item), true));
        }
    }
}
