

package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.ObservableList;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.basic.ProxyDialog;
import rest.view.model.RequestBinding;

/**
 * for config proxy,make proxy host empty to disable proxy
 */
public class ProxyMenu extends SuperMenuItem {
    public ProxyMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("proxy");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        ProxyDialog.ProxyModel proxyModel = new ProxyDialog.ProxyModel();
        if (bindings.size() == 1) {
            proxyModel.proxy(bindings.get(0).getProxy());
            proxyModel.forAll(false);
        } else {
            proxyModel.forAll(true);
        }
        ProxyDialog dialog = new ProxyDialog(proxyModel);
        dialog.showAndWait().ifPresent(proxyModel1 -> {
            if (proxyModel1.forAll()) {
                requestTable.getTable().getItems().forEach(requestBinding -> requestBinding.setProxy(proxyModel1.proxy()));
            } else {
                bindings.forEach(binding -> binding.setProxy(proxyModel1.proxy()));
            }
        });

    }
}
