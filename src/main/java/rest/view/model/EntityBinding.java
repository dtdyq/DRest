

package rest.view.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EntityBinding {

    private List<FieldChangeListener<EntityBinding>> listeners = new ArrayList<>();

    private SimpleStringProperty name = new SimpleStringProperty("default");

    private SimpleListProperty<RequestBinding> requests =
        new SimpleListProperty<>(FXCollections.observableArrayList(param -> new Observable[] {param.nameProperty(),
            param.basicAuthProperty(), param.bodyProperty(), param.codeProperty(), param.formProperty(),
            param.headerProperty(), param.formProperty(), param.proxyProperty(), param.responseProperty(),
            param.respHeaderProperty(), param.openProperty(), param.currentProperty()}));

    private SimpleMapProperty<String, String> variable = new SimpleMapProperty<>();
    private SimpleBooleanProperty open = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty current = new SimpleBooleanProperty(false);

    public EntityBinding() {
        requests.addListener(new ChangeListener<ObservableList<RequestBinding>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<RequestBinding>> observable,
                ObservableList<RequestBinding> oldValue, ObservableList<RequestBinding> newValue) {
                EntityBinding.this.changed("requests", oldValue, newValue);
            }
        });
    }

    public void addListener(FieldChangeListener<EntityBinding> listener) {
        listeners.add(listener);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        String old = getName();
        this.name.set(name);
        changed("name", old, name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public ObservableList<RequestBinding> getRequests() {
        return requests;
    }

    public void addRequests(List<RequestBinding> requests) {
        this.requests.addAll(requests);
    }

    public void addRequest(RequestBinding binding) {
        this.requests.add(binding);
    }

    public void removeRequests(List<RequestBinding> requests) {
        this.requests.removeAll(requests);
    }

    public void removeRequest(RequestBinding binding) {
        this.requests.remove(binding);
    }

    public void changed(String propName, Object old, Object neu) {
        listeners.forEach(new Consumer<FieldChangeListener<EntityBinding>>() {
            @Override
            public void accept(FieldChangeListener<EntityBinding> listener) {
                listener.change(EntityBinding.this, propName, old, neu);
            }
        });
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EntityBinding.class.getSimpleName() + "[", "]").add("listeners=" + listeners)
            .add("name=" + name)
            .add("requests=" + requests)
            .toString();
    }

    public boolean contain(String s) {
        return requests.stream().anyMatch(http -> http.getName().equals(s));
    }

    public void addRequest(int i, RequestBinding binding) {
        this.requests.add(i, binding);
    }

    public boolean isOpen() {
        return open.get();
    }

    public void setOpen(boolean open) {
        this.open.set(open);
    }

    public SimpleBooleanProperty openProperty() {
        return open;
    }

    public boolean isCurrent() {
        return current.get();
    }

    public void setCurrent(boolean current) {
        this.current.set(current);
    }

    public SimpleBooleanProperty currentProperty() {
        return current;
    }

    public void removeRequestByName(String name) {
        this.requests.removeIf(new Predicate<RequestBinding>() {
            @Override
            public boolean test(RequestBinding binding) {
                return binding.getName().equals(name);
            }
        });
    }

    public ObservableMap<String, String> getVariable() {
        return variable.get();
    }

    public void setVariable(Map<String, String> variable) {
        ObservableMap<String, String> old = getVariable();
        this.variable.set(FXCollections.observableMap(variable));
        changed("variable", old, getVariable());
    }

    public SimpleMapProperty<String, String> variableProperty() {
        return variable;
    }
}
