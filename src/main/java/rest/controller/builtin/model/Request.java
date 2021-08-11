

package rest.controller.builtin.model;

import rest.persitence.model.BasicAuth;
import rest.persitence.model.Proxy;

import java.util.Map;
import java.util.StringJoiner;

/**
 * request model:just used for http request
 */
public class Request {
    private String method = "get";
    private String url;
    private String body;
    private Map<String, String> header;
    private Map<String, String> form;
    private BasicAuth basicAuth;
    private Proxy proxy;
    private boolean verify = false;


    @Override
    public String toString() {
        return new StringJoiner(", ", Request.class.getSimpleName() + "[", "]").add("method='" + method + "'")
            .add("url='" + url + "'")
            .add("body='" + body + "'")
            .add("header=" + header)
            .add("basicAuth=" + basicAuth)
            .add("proxy=" + proxy)
            .add("verify=" + verify)
            .toString();
    }

    public String method() {
        return method;
    }

    public Request method(String method) {
        this.method = method;
        return this;
    }

    public String url() {
        return url;
    }

    public Request url(String url) {
        this.url = url;
        return this;
    }

    public String body() {
        return body;
    }

    public Request body(String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> header() {
        return header;
    }

    public Request header(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public BasicAuth basicAuth() {
        return basicAuth;
    }

    public Request basicAuth(BasicAuth basicAuth) {
        this.basicAuth = basicAuth;
        return this;
    }

    public boolean verify() {
        return verify;
    }

    public Request verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    public Proxy proxy() {
        return proxy;
    }

    public Request proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public Map<String, String> form() {
        return form;
    }

    public Request form(Map<String, String> form) {
        this.form = form;
        return this;
    }
}
