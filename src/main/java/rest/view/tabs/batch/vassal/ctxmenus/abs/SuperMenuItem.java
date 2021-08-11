
package rest.view.tabs.batch.vassal.ctxmenus.abs;

import static rest.util.CommonUtil.REST_ENTITY_NAME_PAT;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import rest.util.CommonUtil;
import rest.util.ViewUtil;
import rest.view.basic.JFXTextInputDialog;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.model.RequestBinding;

/**
 * abstract menu item
 */
public abstract class SuperMenuItem extends MenuItem {
    private final BatchRequestTable requestTable;

    public SuperMenuItem(BatchRequestTable requestTable) {
        this.requestTable = requestTable;
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<RequestBinding> binding = requestTable.getTable().getSelectionModel().getSelectedItems();
                if (binding == null) {
                    binding = FXCollections.observableArrayList();
                }
                onRequest(requestTable, binding);
            }
        });
    }

    public abstract void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings);

    protected JFXTextInputDialog inputRestNameDialog() {
        JFXTextInputDialog dialog = new JFXTextInputDialog();
        ButtonType ok = ViewUtil.customDialog(dialog);
        dialog.getDialogPane().lookupButton(ok).setDisable(true);
        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(CommonUtil.noNullTrimStr(newValue)) || !newValue.matches(REST_ENTITY_NAME_PAT)|| requestTable.getEntityBinding()
                .getRequests()
                .stream()
                .anyMatch(r -> r.getName().equals(CommonUtil.noNullTrimStr(newValue)))) {
                dialog.getDialogPane().lookupButton(ok).setDisable(true);
            } else {
                dialog.getDialogPane().lookupButton(ok).setDisable(false);
            }
        });
        dialog.setTitle("rest name");
        return dialog;
    }
}
