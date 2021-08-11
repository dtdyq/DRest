



package rest.view.basic;

import com.jfoenix.controls.JFXCheckBox;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import rest.persitence.model.Proxy;
import rest.util.ViewUtil;

/**
 * dialog for proxy
 */
public class ProxyDialog extends Dialog<ProxyDialog.ProxyModel> {

    public ProxyDialog(ProxyModel proxyModel) {
        Proxy proxy = proxyModel.proxy;
        ButtonType ok = ViewUtil.customDialog(this);

        ProxyVBox display = new ProxyVBox();
        display.render(proxy);

        JFXCheckBox forAll = new JFXCheckBox("config for all item!");
        forAll.setTextFill(Color.RED);
        forAll.setSelected(proxyModel.forAll);
        HBox hBox = ViewUtil.customHBox();
        hBox.getChildren().add(forAll);
        hBox.setPadding(new Insets(0,0,0,20));

        display.getChildren().add(hBox);

        setResultConverter(param -> {
            if (param == ok) {
                ProxyModel model = new ProxyModel();
                model.forAll(forAll.isSelected());
                model.proxy(display.persist());
                return model;
            }
            return null;
        });

        setTitle("proxy config");
        getDialogPane().setContent(display);
    }


    public static class ProxyModel {
        private Proxy proxy = new Proxy();
        private boolean forAll;

        public boolean forAll() {
            return forAll;
        }

        public ProxyModel forAll(boolean forAll) {
            this.forAll = forAll;
            return this;
        }

        public Proxy proxy() {
            return proxy;
        }

        public ProxyModel proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }
    }
}
