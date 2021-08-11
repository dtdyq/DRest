
package rest.view.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import rest.persitence.model.BasicAuth;
import rest.persitence.model.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * request model for view
 */
public class RequestBinding {

    private final SimpleStringProperty name = new SimpleStringProperty("default");

    private final SimpleStringProperty method = new SimpleStringProperty("get");

    private final SimpleStringProperty url = new SimpleStringProperty("");

    private final SimpleListProperty<KeyValue> header = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final SimpleListProperty<KeyValue> form = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final SimpleStringProperty body = new SimpleStringProperty("");

    private final SimpleStringProperty code = new SimpleStringProperty("---");

    private final SimpleStringProperty time = new SimpleStringProperty("---");

    private final SimpleStringProperty response = new SimpleStringProperty("");

    private final SimpleListProperty<KeyValue> respHeader =
        new SimpleListProperty<>(FXCollections.observableArrayList());

    private final SimpleObjectProperty<BasicAuth> basicAuth = new SimpleObjectProperty<>(new BasicAuth());

    private final SimpleObjectProperty<Proxy> proxy = new SimpleObjectProperty<>(new Proxy());

    private final SimpleBooleanProperty verify = new SimpleBooleanProperty(false);

    private final SimpleBooleanProperty open = new SimpleBooleanProperty(false);

    private final SimpleBooleanProperty current = new SimpleBooleanProperty(false);

    private final SimpleObjectProperty<RunEvent> runEvent = new SimpleObjectProperty<>();

    private List<FieldChangeListener<RequestBinding>> listeners = new ArrayList<>();

    private FieldChangeListener<RequestBinding> runEventListener = null;

    public RequestBinding() {

    }

    public RequestBinding(String name) {
        this.name.set(name);
    }

    public RequestBinding(RequestBinding binding) {
        this.setName(binding.getName());
        this.setMethod(binding.getMethod());
        this.setUrl(binding.getUrl());
        this.setBody(binding.getBody());
        this.setHeader(FXCollections.observableList(binding.getHeader()));
        this.setForm(FXCollections.observableList(binding.getForm()));
        setProxy(new Proxy(binding.getProxy()));
        setBasicAuth(new BasicAuth(binding.getBasicAuth()));
        setVerify(binding.isVerify());
        setResponse(binding.getResponse());
        setRespHeader(FXCollections.observableList(binding.getRespHeader()));
    }

    public void addLister(FieldChangeListener<RequestBinding> listener) {
        listeners.add(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestBinding binding = (RequestBinding) o;
        return name.equals(binding.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
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

    public String getMethod() {
        return method.get();
    }

    public void setMethod(String method) {
        String old = getMethod();
        if (old.equals(method)) {
            return;
        }
        this.method.set(method);
        changed("method", old, method);
    }

    public SimpleStringProperty methodProperty() {
        return method;
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        String old = getUrl();
        this.url.set(url);
        changed("url", old, url);
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public ObservableList<KeyValue> getHeader() {
        return header.get();
    }

    public void setHeader(List<KeyValue> header) {
        ObservableList<KeyValue> old = getHeader();
        this.header.set(FXCollections.observableList(header));
        changed("header", old, header);
    }

    public SimpleListProperty<KeyValue> headerProperty() {
        return header;
    }

    public ObservableList<KeyValue> getForm() {
        return form.get();
    }

    public void setForm(List<KeyValue> form) {
        ObservableList<KeyValue> old = getHeader();
        this.form.set(FXCollections.observableList(form));
        changed("form", old, form);
    }

    public SimpleListProperty<KeyValue> formProperty() {
        return form;
    }

    public String getBody() {
        return body.get();
    }

    public void setBody(String body) {
        String old = getBody();
        this.body.set(body);
        changed("body", old, body);
    }

    public SimpleStringProperty bodyProperty() {
        return body;
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        String old = getCode();
        this.code.set(code);
        changed("code", old, code);
    }

    public SimpleStringProperty codeProperty() {
        return code;
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String time) {
        String old = getTime();
        this.time.set(time);
        changed("time", old, time);
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public String getResponse() {
        return response.get();
    }

    public void setResponse(String response) {
        String old = getResponse();
        this.response.set(response);
        changed("response", old, response);
    }

    public SimpleStringProperty responseProperty() {
        return response;
    }

    public ObservableList<KeyValue> getRespHeader() {
        return respHeader.get();
    }

    public void setRespHeader(List<KeyValue> respHeader) {
        ObservableList<KeyValue> old = getRespHeader();
        this.respHeader.set(FXCollections.observableList(respHeader));
        changed("respHeader", old, respHeader);
    }

    public SimpleListProperty<KeyValue> respHeaderProperty() {
        return respHeader;
    }

    public BasicAuth getBasicAuth() {
        return basicAuth.get();
    }

    public void setBasicAuth(BasicAuth basicAuth) {
        if (basicAuth == null) {
            return;
        }
        BasicAuth old = getBasicAuth();
        BasicAuth auth = new BasicAuth(basicAuth);
        this.basicAuth.set(auth);
        changed("basicAuth", old, auth);
    }

    SimpleObjectProperty<BasicAuth> basicAuthProperty() {
        return basicAuth;
    }

    public Proxy getProxy() {
        return proxy.get();
    }

    public void setProxy(Proxy proxy) {
        if (proxy == null) {
            return;
        }
        Proxy old = getProxy();
        Proxy dup = new Proxy(proxy);
        this.proxy.set(dup);
        changed("proxy", old, dup);
    }

    SimpleObjectProperty<Proxy> proxyProperty() {
        return proxy;
    }

    public boolean isVerify() {
        return verify.get();
    }

    public void setVerify(boolean verify) {
        boolean old = isVerify();
        this.verify.set(verify);
        changed("verify", old, verify);
    }

    public SimpleBooleanProperty verifyProperty() {
        return verify;
    }

    public boolean isOpen() {
        return open.get();
    }

    public void setOpen(boolean open) {
        boolean old = isOpen();
        this.open.set(open);
        changed("open", old, open);
    }

    SimpleBooleanProperty openProperty() {
        return open;
    }

    public boolean isCurrent() {
        return current.get();
    }

    public void setCurrent(boolean current) {
        boolean old = isCurrent();
        this.current.set(current);
        changed("current", old, current);
    }

    SimpleBooleanProperty currentProperty() {
        return current;
    }

    private RunEvent getRunEvent() {
        return runEvent.get();
    }

    public void setRunEvent(RunEvent runEvent) {
        RunEvent old = getRunEvent();
        this.runEvent.set(runEvent);
        runEventListener.change(this, "runEvent", old, runEvent);
    }

    private void changed(String propName, Object old, Object neu) {
        RequestBinding binding = this;
        listeners.forEach(listener -> listener.change(binding, propName, old, neu));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RequestBinding.class.getSimpleName() + "[", "]").add("listeners=" + listeners)
            .add("name=" + name)
            .add("method=" + method)
            .add("url=" + url)
            .add("header=" + header)
            .add("form=" + form)
            .add("body=" + body)
            .add("code=" + code)
            .add("time=" + time)
            .add("response=" + response)
            .add("respHeader=" + respHeader)
            .add("basicAuth=" + basicAuth)
            .add("proxy=" + proxy)
            .add("verify=" + verify)
            .add("open=" + open)
            .add("current=" + current)
            .add("runEvent=" + runEvent)
            .toString();
    }

    public void runEventListener(FieldChangeListener<RequestBinding> runEventListener) {
        this.runEventListener = runEventListener;
    }

    public static class RunEvent {
        private int index;
        private boolean sequential;

        public RunEvent(int index, boolean sequential) {
            this.index = index;
            this.sequential = sequential;
        }

        public int getIndex() {
            return index;
        }

        public boolean isSequential() {
            return sequential;
        }

    }
}
