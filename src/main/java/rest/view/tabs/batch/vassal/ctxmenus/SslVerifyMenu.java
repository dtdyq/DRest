

package rest.view.tabs.batch.vassal.ctxmenus;

import com.jfoenix.controls.JFXCheckBox;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import rest.util.ViewUtil;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.ctxmenus.abs.SuperMenuItem;
import rest.view.model.RequestBinding;

/**
 * edit ssl verify
 */
public class SslVerifyMenu extends SuperMenuItem {

    public SslVerifyMenu(BatchRequestTable requestTable) {
        super(requestTable);
        setText("ssl verify");
    }

    @Override
    public void onRequest(BatchRequestTable requestTable, ObservableList<RequestBinding> bindings) {
        if (bindings.size() == 1) {
            RequestBinding binding = bindings.get(0);
            Dialog<EnableSsl> dialog = new Dialog<>();
            dialog.setTitle("ssl verify config");
            ButtonType ok = ViewUtil.customDialog(dialog);
            HBox hBox = new HBox();
            JFXCheckBox cur = new JFXCheckBox("enable ssl verify");
            if (binding != null) {
                cur.setSelected(binding.isVerify());
            }
            JFXCheckBox all = new JFXCheckBox("apply for all item!");
            hBox.getChildren().addAll(cur, all);
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(5));
            dialog.getDialogPane().setContent(hBox);
            dialog.setResultConverter(param -> {
                if (param == ok) {
                    EnableSsl ssl = new EnableSsl();
                    ssl.enableCur(cur.isSelected());
                    ssl.enableAll(all.isSelected());
                    return ssl;
                }
                return null;
            });
            dialog.showAndWait().ifPresent(enableSsl -> {
                if (binding != null) {
                    binding.setVerify(enableSsl.enableCur);
                }
                if (enableSsl.enableAll) {
                    requestTable.getTable().getItems().forEach(binding1 -> binding1.setVerify(enableSsl.enableCur));
                }
            });
        }
    }

    private class EnableSsl {
        private boolean enableCur;
        private boolean enableAll;

        public boolean enableAll() {
            return enableAll;
        }

        public EnableSsl enableAll(boolean enableAll) {
            this.enableAll = enableAll;
            return this;
        }

        public boolean enableCur() {
            return enableCur;
        }

        public EnableSsl enableCur(boolean enableCur) {
            this.enableCur = enableCur;
            return this;
        }
    }
}
