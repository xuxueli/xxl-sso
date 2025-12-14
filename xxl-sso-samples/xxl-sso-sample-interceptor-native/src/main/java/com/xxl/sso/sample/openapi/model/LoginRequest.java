package com.xxl.sso.sample.openapi.model;

/**
 * @author xuxueli 2018-03-22 23:51:51
 */
public class LoginRequest {

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    public LoginRequest() {
    }
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
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

}
