package com.xxl.sso.core.user;

import java.io.Serializable;
import java.util.Map;

/**
 * xxl sso user
 *
 * @author xuxueli 2018-04-02 19:59:49
 */
public class XxlSsoUser implements Serializable {
    private static final long serialVersionUID = 42L;

    // field
    private String userid;
    private String username;
    private String version;

    private Map<String, String> plugininfo;


    // set get
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getPlugininfo() {
        return plugininfo;
    }

    public void setPlugininfo(Map<String, String> plugininfo) {
        this.plugininfo = plugininfo;
    }

}
