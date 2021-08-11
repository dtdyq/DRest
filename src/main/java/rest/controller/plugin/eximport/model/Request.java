


package rest.controller.plugin.eximport.model;

import rest.persitence.model.BasicAuth;
import rest.persitence.model.Proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * rest item is a http request
 */
public class Request {
    private String name = "default";
    private BasicAuth basicAuth;
    private Proxy proxy;
    private boolean verify = false;
    private String method = "get";
    private String url = "";
    private Map<String, String> header = new HashMap<>();
    private Map<String, String> form = new HashMap<>();
    private String body = "";
    private String code = "---";
    private String time = "---";
    private String response = "";
    private Map<String, String> respHeader = new HashMap<>();

    private static String simplifyStr(String s) {
        if (s == null || s.length() <= 6) {
            return s;
        }
        return s.substring(0, 3) + "...";
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

    public Map<String, String> header() {
        return header;
    }

    public Request header(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public String body() {
        return body;
    }

    public Request body(String body) {
        this.body = body;
        return this;
    }

    public String code() {
        return code;
    }

    public Request code(String code) {
        this.code = code;
        return this;
    }

    public String time() {
        return time;
    }

    public Request time(String time) {
        this.time = time;
        return this;
    }

    public String response() {
        return response;
    }

    public Request response(String response) {
        this.response = response;
        return this;
    }

    public Map<String, String> respHeader() {
        return respHeader;
    }

    public Request respHeader(Map<String, String> respHeader) {
        this.respHeader = respHeader;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Request.class.getSimpleName() + "[", "]").add("basicAuth=" + basicAuth)
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

    public Request proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public String name() {
        return name;
    }

    public Request name(String name) {
        this.name = name;
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
