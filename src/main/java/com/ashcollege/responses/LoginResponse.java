package com.ashcollege.responses;

import com.ashcollege.entities.User;

public class LoginResponse extends BasicResponse {
    private int id;
    private String secret;

    private User user;

    public LoginResponse(boolean success, Integer errorCode, Integer id, String secret, User user) {
        super(success, errorCode);
        this.id = id;
        this.secret = secret;
        this.user = user;
    }

    public LoginResponse(boolean success, Integer errorCode) {
        super(success, errorCode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
