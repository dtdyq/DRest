


package rest.view.basic;

import com.jfoenix.controls.JFXCheckBox;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import rest.persitence.model.BasicAuth;
import rest.util.ViewUtil;

/**
 * dialog for basic auth
 */
public class BasicAuthDialog extends Dialog<BasicAuthDialog.BasicAuthModel> {
    public BasicAuthDialog(BasicAuthModel basicAuthModel) {
        BasicAuth basicAuth = basicAuthModel.basicAuth;
        ButtonType ok = ViewUtil.customDialog(this);

        JFXCheckBox forAll = new JFXCheckBox("config for all item!");
        forAll.setSelected(basicAuthModel.forAll);

        HBox hBox = ViewUtil.customHBox();
        hBox.getChildren().add(forAll);
        hBox.setPadding(new Insets(0,0,0,20));

        BasicAuthVBox display = new BasicAuthVBox();
        display.render(basicAuth);
        display.getChildren().add(hBox);

        setResultConverter(param -> {
            if (param == ok) {
                BasicAuthModel model = new BasicAuthModel();
                model.basicAuth(display.persist());
                model.forAll(forAll.isSelected());
                return model;
            } else {
                return null;
            }
        });

        setTitle("basic auth config");
        getDialogPane().setContent(display);
    }

    public static class BasicAuthModel {
        private BasicAuth basicAuth = new BasicAuth();
        private boolean forAll = false;

        public boolean forAll() {
            return forAll;
        }

        public BasicAuthModel forAll(boolean forAll) {
            this.forAll = forAll;
            return this;
        }

        public BasicAuth basicAuth() {
            return basicAuth;
        }

        public BasicAuthModel basicAuth(BasicAuth basicAuth) {
            this.basicAuth = basicAuth;
            return this;
        }
    }
}
