

package rest.controller.builtin.process;

public interface Processer<K, V> {
    V process(K k);
}
