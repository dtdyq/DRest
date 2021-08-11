

package rest.view.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.StringJoiner;

public class KeyValue {
    private final SimpleStringProperty key;
    private final SimpleStringProperty value;

    public KeyValue(String key, String value) {
        this.key = new SimpleStringProperty(key);
        this.value = new SimpleStringProperty(value);
    }

    public String getKey() {
        return key.get();
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KeyValue.class.getSimpleName() + "[", "]").add("key=" + getKey())
            .add("value=" + getValue())
            .toString();
    }
}
