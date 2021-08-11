

package rest.persitence.model;

import java.util.StringJoiner;

/**
 * basic auth model
 */
public class BasicAuth {
    private String username = "";
    private String password = "";

    public BasicAuth() {
    }

    public BasicAuth(BasicAuth basicAuth) {
        this.username = basicAuth.username;
        this.password = basicAuth.password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BasicAuth.class.getSimpleName() + "[", "]").add("username='" + username + "'")
            .add("password='" + password + "'")
            .toString();
    }
}
