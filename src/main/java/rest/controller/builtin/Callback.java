

package rest.controller.builtin;

import rest.controller.builtin.model.Response;

public interface Callback {
    void call(Response response);
}
