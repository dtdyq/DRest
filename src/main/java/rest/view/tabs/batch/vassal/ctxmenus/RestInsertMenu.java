
package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.util.CommonUtil;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

/**
 * insert new rest item to bottom of selected item
 */
public class RestInsertMenu extends SuperMenuItem {
    public RestInsertMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("insert");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        inputRestNameDialog().showAndWait().ifPresent(s -> {
            RequestBinding binding = new RequestBinding();
            binding.setName(CommonUtil.noNullTrimStr(s));
            if (!bindings.isEmpty()) {
                requestTable.getEntityBinding()
                    .addRequest(
                        requestTable.getEntityBinding().getRequests().indexOf(bindings.get(bindings.size() - 1)) + 1,
                        binding);
            } else {
                requestTable.getEntityBinding().addRequest(binding);
            }
        });

    }
}
