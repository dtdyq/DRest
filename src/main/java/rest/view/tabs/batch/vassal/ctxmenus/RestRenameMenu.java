

package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.util.CommonUtil;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

/**
 * rename the selected rest item
 */
public class RestRenameMenu extends SuperMenuItem {

    public RestRenameMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("rename");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        if (bindings.size() == 1) {
            RequestBinding binding = bindings.get(0);
            inputRestNameDialog().showAndWait().ifPresent(s -> {
                binding.setName(CommonUtil.noNullTrimStr(s));
            });
        }
    }
}
