


package rest.controller.plugin.eximport.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * one rest entity contains a list of rest item
 */
public class Entity {

    private String name = "default";
    private List<Request> requests = new ArrayList<>();
    private Map<String,String> variable = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Request> getBatch() {
        return requests;
    }

    public void setBatch(List<Request> batch) {
        this.requests = batch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Entity that = (Entity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Map<String, String> variable() {
        return variable;
    }

    public Entity variable(Map<String, String> variable) {
        this.variable = variable;
        return this;
    }
}
