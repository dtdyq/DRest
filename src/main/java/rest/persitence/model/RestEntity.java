
package rest.persitence.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * one rest entity contains a list of rest item
 */
public class RestEntity {

    private String name = "default";
    private boolean opened = true;
    private boolean current = true;
    private List<RestItem> batch = new ArrayList<>();
    private Map<String,String> variable = new HashMap<>();
    @Override
    public String toString() {
        return new StringJoiner(", ", RestEntity.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("opened=" + opened)
            .add("current=" + current)
            .add("batch=" + batch)
            .toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public List<RestItem> getBatch() {
        return batch;
    }

    public void setBatch(List<RestItem> batch) {
        this.batch = batch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RestEntity that = (RestEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Map<String, String> variable() {
        return variable;
    }

    public RestEntity variable(Map<String, String> variable) {
        this.variable = variable;
        return this;
    }
}
