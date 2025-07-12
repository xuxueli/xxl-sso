package com.xxl.sso.sample.weblogin.model;

/**
 * @author xuxueli 2018-03-22 23:51:51
 */
public class AccountInfo {

    /**
     * user id
     */
    private String userid;

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;


    public AccountInfo() {
    }
    public AccountInfo(String userid, String username, String password) {
        this.userid = userid;
        this.username = username;
        this.password = password;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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
