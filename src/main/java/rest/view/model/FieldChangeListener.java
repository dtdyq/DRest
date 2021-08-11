

package rest.view.model;

public interface FieldChangeListener<T> {
    void change(T src, String propName, Object old, Object neu);
}