package com.example.passmanager.model;

public class User {

    private String uid;
    private String usernmae;

    public User() { }

    public User(String uid, String usernmae) {
        this.uid = uid;
        this.usernmae = usernmae;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsernmae() {
        return usernmae;
    }

    public void setUsernmae(String usernmae) {
        this.usernmae = usernmae;
    }
}
