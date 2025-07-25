package com.xxl.sso.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * xxl sso login info
 *
 * @author xuxueli 2018-04-02 19:59:49
 */
public class LoginInfo implements Serializable {
    private static final long serialVersionUID = 42L;


    public LoginInfo() {
    }
    // for token
    public LoginInfo(String userId, String version) {
        this.userId = userId;
        this.version = version;
    }
    public LoginInfo(String userId, String userName, String realName, Map<String, String> extraInfo, List<String> roleList, List<String> permissionList, long expireTime, String version) {
        this.userId = userId;
        this.userName = userName;
        this.realName = realName;
        this.extraInfo = extraInfo;
        this.roleList = roleList;
        this.permissionList = permissionList;
        this.expireTime = expireTime;
        this.version = version;
    }

    // ---------------------- user info ----------------------

    /**
     * user id
     */
    private String userId;

    /**
     * user name
     */
    private String userName;

    /**
     * real name
     */
    private String realName;

    /**
     * extra info
     */
    private Map<String, String> extraInfo;


    // ---------------------- role / permission ----------------------

    /**
     * role list
     */
    private List<String> roleList;

    /**
     * permission list
     */
    private List<String> permissionList;


    // ---------------------- setting ----------------------

    /**
     * expire time
     */
    private long expireTime;

    /**
     * version
     */
    private String version;

    /**
     * auto renew
     */
    private boolean autoRenew;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
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

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", realName='" + realName + '\'' +
                ", extraInfo=" + extraInfo +
                ", roleList=" + roleList +
                ", permissionList=" + permissionList +
                ", expireTime='" + expireTime + '\'' +
                ", version=" + version +
                ", autoRenew=" + autoRenew +
                '}';
    }

}
