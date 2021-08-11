

package rest.view.basic;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * enhanced passwordField.support show/hidden password
 */
public class MaskField extends HBox {
    private JFXTextField un = new JFXTextField();
    private JFXPasswordField mask = new JFXPasswordField();

    public MaskField() {
        un.setPromptText("Password");
        un.setLabelFloat(true);
        un.setPadding(new Insets(18,18,0,18));
        mask.setLabelFloat(true);
        mask.setPromptText("Password");
        mask.setPadding(new Insets(18,18,0,18));
        JFXCheckBox checkBox = new JFXCheckBox();
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(mask);
        getChildren().addAll(stackPane, checkBox);
        un.textProperty().bindBidirectional(mask.textProperty());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                stackPane.getChildren().clear();
                stackPane.getChildren().add(un);
            } else {
                stackPane.getChildren().clear();
                stackPane.getChildren().add(mask);
            }
        });
        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);
    }

    public String getText() {
        return mask.getText();
    }

    public void setText(String text) {
        mask.setText(text);
    }
}
