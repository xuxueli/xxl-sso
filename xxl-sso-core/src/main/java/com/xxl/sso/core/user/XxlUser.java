package com.xxl.sso.core.user;

import java.io.Serializable;

/**
 * xxl sso user
 *
 * @author xuxueli 2018-04-02 19:59:49
 */
public class XxlUser implements Serializable {
    private static final long serialVersionUID = 42L;

    private int userid;
    private String username;

    // TODO，custome

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
