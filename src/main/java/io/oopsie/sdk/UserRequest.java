package io.oopsie.sdk;

import java.util.Set;

/**
 * Holds information for registering a site user.
 */
public class UserRequest {
    
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<String> authz;

    public UserRequest() {
    }

    public UserRequest(String email, String password, String firstName, String lastName, Set<String> authz) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<String> getAuthz() {
        return authz;
    }

    public void setAuthz(Set<String> authz) {
        this.authz = authz;
    }
}
