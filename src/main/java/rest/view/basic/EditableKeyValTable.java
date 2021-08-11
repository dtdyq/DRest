
package rest.view.basic;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTableCell;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import rest.util.ViewUtil;
import rest.view.model.KeyValue;

/**
 * editable key value table
 * 
 * @see KeyValTable
 */
public class EditableKeyValTable extends KeyValTable {
    private MenuItem add = new MenuItem("add");
    private MenuItem delete = new MenuItem("delete");
    private MenuItem paste = new MenuItem("paste from kv text");

    public EditableKeyValTable() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);
        key.setCellFactory(param -> new GenericEditableTableCell<>(new TextFieldEditorBuilder()));
        key.setOnEditCommit(event -> event.getTableView()
            .getItems()
            .get(event.getTablePosition().getRow())
            .setKey(event.getNewValue().trim()));
        value.setCellFactory(param -> new GenericEditableTableCell<>(new TextFieldEditorBuilder()));
        value.setOnEditCommit(event -> event.getTableView()
            .getItems()
            .get(event.getTablePosition().getRow())
            .setValue(event.getNewValue().trim()));

        add.setOnAction(event -> {
            Dialog<String> dialog = new Dialog<>();
            ButtonType ok = ViewUtil.customDialog(dialog);

            HBox hBox = ViewUtil.customHBox();
            JFXTextField key = new JFXTextField();
            key.setMinWidth(150);
            key.setPromptText("key");
            JFXTextField val = new JFXTextField();
            val.setMinWidth(150);
            val.setPromptText("value");
            hBox.getChildren().addAll(key, val);

            dialog.getDialogPane().setContent(hBox);
            dialog.setResultConverter(new Callback<ButtonType, String>() {
                @Override
                public String call(ButtonType param) {
                    if (param == ok) {
                        getItems().add(new KeyValue(key.getText().trim(), val.getText().trim()));
                    }
                    return null;
                }
            });
            dialog.showAndWait();
        });

        delete.setOnAction(event -> getItems().removeAll(getSelectionModel().getSelectedItems()));

        paste.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                String text = clipboard.getString();
                for (String kv : text.split(System.lineSeparator())) {
                    if (kv.contains(":")) {
                        String k = kv.split(":")[0].trim();
                        if (!"".equals(k) && k.split("\\s+").length == 1) {
                            getItems().add(new KeyValue(kv.split(":")[0], kv.split(":")[1]));
                        }
                    }
                }
            }
        });
        contextMenu.getItems().addAll(add, delete, paste);
    }

}
