

package rest.view.basic;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import rest.view.model.KeyValue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * key value field table and can not edit,support copy to plan text
 */
public class KeyValTable extends TableView<KeyValue> {
    protected TableColumn<KeyValue,String> key = new TableColumn<>("key");
    protected TableColumn<KeyValue,String> value = new TableColumn<>("value");

    protected ContextMenu contextMenu = new ContextMenu();
    public KeyValTable() {
        setPlaceholder(new Label(""));
        key.setCellValueFactory(new PropertyValueFactory<>("key"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        getColumns().addAll(key, value);
        key.prefWidthProperty().bind(widthProperty().multiply(0.4));
        value.prefWidthProperty().bind(widthProperty().multiply(0.6));
        MenuItem menu = new MenuItem("copy as plain text");
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String text = getItems().stream()
                    .map(kv -> kv.getKey() + ":" + kv.getValue())
                    .collect(Collectors.joining(System.lineSeparator()));
                Clipboard clipboard = Clipboard.getSystemClipboard();
                Map<DataFormat, Object> map = new HashMap<>();
                map.put(DataFormat.PLAIN_TEXT, text);
                clipboard.setContent(map);
            }
        });
        contextMenu.getItems().add(menu);
        setContextMenu(contextMenu);
    }
}
