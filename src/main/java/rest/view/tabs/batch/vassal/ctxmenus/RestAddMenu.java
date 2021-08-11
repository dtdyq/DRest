

package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCombination;
import rest.util.CommonUtil;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

/**
 * append new rest item
 */
public class RestAddMenu extends SuperMenuItem {

    public RestAddMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("add");
        setAccelerator(KeyCombination.valueOf("ctrl+r"));
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> tmp) {
        inputRestNameDialog().showAndWait().ifPresent(s -> {
            RequestBinding binding = new RequestBinding();
            binding.setName(CommonUtil.noNullTrimStr(s));
            requestTable.getEntityBinding().addRequest(binding);
        });
    }
}
