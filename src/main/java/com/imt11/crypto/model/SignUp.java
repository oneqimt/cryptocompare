package com.imt11.crypto.model;

/**
 * @author Dennis Miller
 */
public class SignUp {

    private Person person;
    private Auth auth;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "SignUp{" +
                "person=" + person +
                ", auth=" + auth +
                '}';
    }
}
