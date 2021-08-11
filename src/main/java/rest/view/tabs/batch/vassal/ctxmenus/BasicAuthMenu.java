

package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.basic.BasicAuthDialog;
import rest.view.model.RequestBinding;

/**
 * menu item for config basic auth,set basic auth username to empty to disable basic auth
 * @see BasicAuthDialog
 *
 */
public class BasicAuthMenu extends SuperMenuItem {
    public BasicAuthMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("basic auth");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        BasicAuthDialog.BasicAuthModel basicAuthModel = new BasicAuthDialog.BasicAuthModel();
        if (bindings.size() == 1) {
            basicAuthModel.basicAuth(bindings.get(0).getBasicAuth());
            basicAuthModel.forAll(false);
        } else {
            basicAuthModel.forAll(true);
        }
        new BasicAuthDialog(basicAuthModel).showAndWait().ifPresent(basicAuth -> {
            if (basicAuth.forAll()) {
                requestTable.getTable().getItems().forEach(binding1 -> binding1.setBasicAuth(basicAuth.basicAuth()));
            } else {
                bindings.forEach(binding -> binding.setBasicAuth(basicAuth.basicAuth()));
            }
        });
    }
}
