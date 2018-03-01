package io.oopsie.sdk;

import java.util.Set;

/**
 * Holds information for registering a site user.
 */
public class UserRequest {
    
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private Set<String> authz;

    public UserRequest() {
    }

    public UserRequest(String email, String password, String firstname, String lastname, Set<String> authz) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.authz = authz;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Set<String> getAuthz() {
        return authz;
    }

    public void setAuthz(Set<String> authz) {
        this.authz = authz;
    }
}
