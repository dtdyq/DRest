

package rest.view.basic;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import rest.persitence.model.Proxy;
import rest.util.CommonUtil;
import rest.view.model.FieldChangeListener;

import java.util.ArrayList;
import java.util.List;

public class ProxyVBox extends VBox {
    private JFXTextField host = new JFXTextField();
    private JFXTextField port = new JFXTextField();
    private JFXTextField username = new JFXTextField();
    private JFXPasswordField password = new JFXPasswordField();
    private List<FieldChangeListener<Proxy>> listeners = new ArrayList<>();

    public ProxyVBox() {
        setPrefWidth(400);
        setSpacing(10);
        setPadding(new Insets(5));

        host.setPromptText("Host");
        host.setPadding(new Insets(20, 20, 0, 20));
        host.setLabelFloat(true);
        port.setPromptText("Port");
        port.setLabelFloat(true);
        port.setPadding(new Insets(20, 20, 0, 20));
        username.setPromptText("Username");
        username.setLabelFloat(true);
        username.setPadding(new Insets(20, 20, 0, 20));
        password.setPromptText("Password");
        password.setLabelFloat(true);
        password.setPadding(new Insets(20, 20, 0, 20));

        host.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                listeners.forEach(listener -> listener.change(persist(), "host", "", host.getText()));
            }
        });
        port.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                listeners.forEach(listener -> listener.change(persist(), "port", "", port.getText()));
            }
        });
        username.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                listeners.forEach(listener -> listener.change(persist(), "username", "", username.getText()));
            }
        });
        password.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                listeners.forEach(listener -> listener.change(persist(), "password", "", password.getText()));
            }
        });

        port.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (Integer.parseInt(newValue) < 1 || Integer.parseInt(newValue) > 65535) {
                        port.getStyleClass().remove("normal");
                        port.getStyleClass().add("danger");
                    } else {
                        port.getStyleClass().remove("danger");
                        port.getStyleClass().add("normal");
                    }
                } catch (NumberFormatException e) {
                    port.getStyleClass().remove("normal");
                    port.getStyleClass().add("danger");
                }
            }
        });
        getChildren().addAll(host, port, username, password);
    }

    public void render(Proxy proxy) {
        host.setText(proxy.host());
        if (proxy.port() == 0) {
            port.setText("");
        } else {
            port.setText(String.valueOf(proxy.port()));
        }
        username.setText(proxy.username());
        password.setText(proxy.password());
    }

    public Proxy persist() {
        Proxy proxy = new Proxy();
        proxy.host(CommonUtil.noNullTrimStr(host.getText()));
        proxy.port(Integer.parseInt(CommonUtil.noNullTrimStr(port.getText())));
        proxy.username(CommonUtil.noNullTrimStr(username.getText()));
        proxy.password(CommonUtil.noNullTrimStr(password.getText()));
        return proxy;
    }

    public void addListener(FieldChangeListener<Proxy> listener) {
        listeners.add(listener);
    }
}
