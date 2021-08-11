

package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.view.tabs.history.Recoder;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

/**
 * display the operation log and for rollback
 */
public class RestLogMenu extends SuperMenuItem {

    public RestLogMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("operate history");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> binding) {
        Recoder.inst().show();
    }
}
