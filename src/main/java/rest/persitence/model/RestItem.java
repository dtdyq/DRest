

package rest.persitence.model;

import rest.view.model.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * rest item is a http request
 */
public class RestItem {
    private String name = "default";
    private boolean opened = false;
    private boolean current = false;


    private BasicAuth basicAuth = new BasicAuth();
    private Proxy proxy = new Proxy();
    private boolean verify = false;
    private String method = "get";
    private String url = "";
    private List<KeyValue> header = new ArrayList<>();
    private List<KeyValue> form = new ArrayList<>();
    private String body = "";
    private String code = "---";
    private String time = "---";
    private String response = "";
    private List<KeyValue> respHeader = new ArrayList<>();

    private static String simplifyStr(String s) {
        if (s == null || s.length() <= 6) {
            return s;
        }
        return s.substring(0, 3) + "...";
    }

    public BasicAuth basicAuth() {
        return basicAuth;
    }

    public RestItem basicAuth(BasicAuth basicAuth) {
        this.basicAuth = basicAuth;
        return this;
    }

    public boolean verify() {
        return verify;
    }

    public RestItem verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    public String method() {
        return method;
    }

    public RestItem method(String method) {
        this.method = method;
        return this;
    }

    public String url() {
        return url;
    }

    public RestItem url(String url) {
        this.url = url;
        return this;
    }

    public List<KeyValue> header() {
        return header;
    }

    public RestItem header(List<KeyValue> header) {
        this.header = header;
        return this;
    }

    public String body() {
        return body;
    }

    public RestItem body(String body) {
        this.body = body;
        return this;
    }

    public String code() {
        return code;
    }

    public RestItem code(String code) {
        this.code = code;
        return this;
    }

    public String time() {
        return time;
    }

    public RestItem time(String time) {
        this.time = time;
        return this;
    }

    public String response() {
        return response;
    }

    public RestItem response(String response) {
        this.response = response;
        return this;
    }

    public List<KeyValue> respHeader() {
        return respHeader;
    }

    public RestItem respHeader(List<KeyValue> respHeader) {
        this.respHeader = respHeader;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RestItem.class.getSimpleName() + "[", "]").add("basicAuth=" + basicAuth)
            .add("proxy=" + proxy)
            .add("verify=" + verify)
            .add("method='" + method + "'")
            .add("url='" + url + "'")
            .add("header=" + header)
            .add("body='" + simplifyStr(body) + "'")
            .add("code='" + code + "'")
            .add("time='" + time + "'")
            .add("response='" + simplifyStr(response) + "'")
            .add("respHeader=" + respHeader)
            .toString();
    }

    public Proxy proxy() {
        return proxy;
    }

    public RestItem proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public String name() {
        return name;
    }

    public RestItem name(String name) {
        this.name = name;
        return this;
    }

    public List<KeyValue> form() {
        return form;
    }

    public RestItem form(List<KeyValue> form) {
        this.form = form;
        return this;
    }

    public boolean opened() {
        return opened;
    }

    public RestItem opened(boolean opened) {
        this.opened = opened;
        return this;
    }

    public boolean current() {
        return current;
    }

    public RestItem current(boolean current) {
        this.current = current;
        return this;
    }
}
