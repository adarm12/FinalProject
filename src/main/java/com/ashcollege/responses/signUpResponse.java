package com.ashcollege.responses;

public class signUpResponse extends BasicResponse {
    private int id;
    private String secret;

    public signUpResponse(boolean success, Integer errorCode, int id, String secret) {
        super(success, errorCode);
        this.id = id;
        this.secret = secret;
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
}