package pro.shushi.pamirs.sso.common.dto;

import java.io.Serializable;

public class SsoUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String clientId;

    private String state;

    private String redirectUri;

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
