package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class Auth {

    private int auth_id;
    private String username;
    private String password;
    private int person_id;
    private String role;
    private int enabled;

    public Auth() {
    }

    public int getAuth_id() {
        return auth_id;
    }

    public void setAuth_id(int auth_id) {
        this.auth_id = auth_id;
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

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "auth_id=" + auth_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", person_id=" + person_id +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
