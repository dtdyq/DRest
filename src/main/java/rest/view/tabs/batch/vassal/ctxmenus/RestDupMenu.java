
package rest.view.tabs.batch.vassal.ctxmenus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import rest.util.CommonUtil;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * duplicate selected rest items and insert them after be copied
 */
public class RestDupMenu extends SuperMenuItem {
    public RestDupMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("duplicate");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        if (!bindings.isEmpty()) {
            requestTable.getEntityBinding().addRequests(copy(requestTable, bindings));
        }
    }

    private ObservableList<RequestBinding> copy(BatchRequestTable requestTable, ObservableList<RequestBinding> items) {
        ObservableList<RequestBinding> ret = FXCollections.observableArrayList();
        List<String> names = requestTable.getEntityBinding()
            .getRequests()
            .stream()
            .map(RequestBinding::getName)
            .collect(Collectors.toList());
        items.forEach(new Consumer<RequestBinding>() {
            @Override
            public void accept(RequestBinding binding) {
                RequestBinding temp = new RequestBinding(binding);
                String name = CommonUtil.uniqueItemName(names, binding.getName());
                temp.setName(name);
                names.add(name);
                ret.add(temp);
            }
        });
        return ret;
    }
}
