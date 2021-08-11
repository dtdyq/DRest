

package rest.controller.builtin.model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * http response model
 */
public class Response {
    private String code = "---";
    private String body = "internal error";
    private Map<String, String> header = new HashMap<>();
    private long time = 0;

    public String code() {
        return code;
    }

    public Response code(String code) {
        this.code = code;
        return this;
    }

    public String body() {
        return body;
    }

    public Response body(String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> header() {
        return header;
    }

    public Response header(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public long time() {
        return time;
    }

    public Response time(long time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Response.class.getSimpleName() + "[", "]").add("code='" + code + "'")
            .add("body='" + body + "'")
            .add("header=" + header)
            .add("time=" + time)
            .toString();
    }
}
