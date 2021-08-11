

package rest.view.basic;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import rest.persitence.model.BasicAuth;
import rest.util.CommonUtil;
import rest.view.model.FieldChangeListener;

import java.util.ArrayList;
import java.util.List;

public class BasicAuthVBox extends VBox {
    private JFXTextField username = new JFXTextField();
    private JFXPasswordField password = new JFXPasswordField();
    private List<FieldChangeListener<BasicAuth>> listeners = new ArrayList<>();

    public BasicAuthVBox() {
        username.setPromptText("Username");
        username.setLabelFloat(true);
        username.setPadding(new Insets(20, 20, 0, 20));
        username.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                listeners.forEach(listener -> listener.change(persist(), "username", "", username.getText()));
            }
        });

        password.setPromptText("Password");
        password.setLabelFloat(true);
        password.setPadding(new Insets(20, 20, 0, 20));
        password.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                listeners.forEach(listener -> listener.change(persist(), "password", "", password.getText()));
            }
        });

        setPrefWidth(400);
        setSpacing(10);
        setPadding(new Insets(5));
        getChildren().addAll(username, password);
    }

    public void render(BasicAuth basicAuth) {
        username.setText(basicAuth.getUsername());
        password.setText(basicAuth.getPassword());
    }

    public BasicAuth persist() {
        BasicAuth basicAuth = new BasicAuth();
        basicAuth.setUsername(CommonUtil.noNullTrimStr(username.getText()));
        basicAuth.setPassword(CommonUtil.noNullTrimStr(password.getText()));
        return basicAuth;
    }

    public void addListener(FieldChangeListener<BasicAuth> listener) {
        listeners.add(listener);
    }

}
