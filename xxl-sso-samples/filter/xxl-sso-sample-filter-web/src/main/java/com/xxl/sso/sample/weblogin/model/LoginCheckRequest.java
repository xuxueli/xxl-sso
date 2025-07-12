package com.xxl.sso.sample.weblogin.model;

/**
 * @author xuxueli 2018-03-22 23:51:51
 */
public class LoginCheckRequest {

    private String token;

    public LoginCheckRequest() {
    }
    public LoginCheckRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
