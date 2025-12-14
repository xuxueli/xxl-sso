package com.xxl.sso.sample.openapi.model;

import java.util.List;

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

    /**
     * role list
     */
    private List<String> roleList;

    /**
     * permission list
     */
    private List<String> permissionList;


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

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

}
