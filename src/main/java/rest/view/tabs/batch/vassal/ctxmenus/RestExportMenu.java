package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;

public class RestExportMenu extends SuperMenuItem {

    public RestExportMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("export to curl");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        if (bindings.size() == 1) {
            RequestBinding binding = bindings.get(0);

        }
    }
}
