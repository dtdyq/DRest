

package rest.persitence.model;

import java.util.StringJoiner;

/**
 * proxy model
 */
public class Proxy {
    private String host = "";
    private int port;
    private String password = "";
    private String username = "";

    public Proxy() {
    }

    public Proxy(String host, int port, String password, String username) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.username = username;
    }

    public Proxy(Proxy proxy) {
        this.host = proxy.host;
        this.port = proxy.port;
        this.username = proxy.username;
        this.password = proxy.password;
    }

    public String host() {
        return host;
    }

    public Proxy host(String host) {
        this.host = host;
        return this;
    }

    public int port() {
        return port;
    }

    public Proxy port(int port) {
        this.port = port;
        return this;
    }

    public String password() {
        return password;
    }

    public Proxy password(String password) {
        this.password = password;
        return this;
    }

    public String username() {
        return username;
    }

    public Proxy username(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Proxy.class.getSimpleName() + "[", "]").add("host='" + host + "'")
            .add("port=" + port)
            .add("password='" + password + "'")
            .add("username='" + username + "'")
            .toString();
    }
}
