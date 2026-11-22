package com.xxl.sso.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * xxl sso login info
 *
 * <p>
 * 统一登录用户模型：{@code userId + signature} 会写入 token，其余字段（用户名、角色、权限、过期时间）
 * 仅保存在服务端 LoginStore，不随 token 下发。
 * </p>
 *
 * @author xuxueli 2018-04-02 19:59:49
 */
public class LoginInfo implements Serializable {
    private static final long serialVersionUID = 42L;


    /**
     * create empty login info
     */
    public LoginInfo() {
    }

    /**
     * create token-carried login info
     *
     * @param userId        user id
     * @param signature     login signature
     */
    public LoginInfo(String userId, String signature) {
        this.userId = userId;
        this.signature = signature;
    }

    /**
     * create full login info
     *
     * @param userId            user id
     * @param userName          user name
     * @param realName          real name
     * @param extraInfo         extra info
     * @param roleList          role list
     * @param permissionList    permission list
     * @param expireTime        expire time, epoch seconds
     * @param signature         login signature
     */
    public LoginInfo(String userId,
                     String userName,
                     String realName,
                     Map<String, String> extraInfo,
                     List<String> roleList,
                     List<String> permissionList,
                     long expireTime,
                     String signature) {
        this.userId = userId;
        this.userName = userName;
        this.realName = realName;
        this.extraInfo = extraInfo;
        this.roleList = roleList;
        this.permissionList = permissionList;
        this.expireTime = expireTime;
        this.signature = signature;
    }

    // ---------------------- user info ----------------------

    /**
     * user id, unique
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
     * extra info, nullable
     */
    private Map<String, String> extraInfo;


    // ---------------------- role / permission ----------------------

    /**
     * role list, nullable
     */
    private List<String> roleList;

    /**
     * permission list, nullable
     */
    private List<String> permissionList;


    // ---------------------- setting ----------------------

    /**
     * expire time, epoch seconds; login state is invalid after this time
     */
    private long expireTime;

    /**
     * login signature; carried by token and compared with stored value on login check
     */
    private String signature;


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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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
                ", expireTime=" + expireTime +
                ", signature='" + signature + '\'' +
                '}';
    }

}
