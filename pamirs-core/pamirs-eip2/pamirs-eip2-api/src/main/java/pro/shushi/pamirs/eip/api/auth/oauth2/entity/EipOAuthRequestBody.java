package pro.shushi.pamirs.eip.api.auth.oauth2.entity;

import pro.shushi.pamirs.eip.api.entity.AbstractEipHttpRequestBody;

import java.io.Serializable;

/**
 * @author Adamancy Zhang on 2021-02-05 16:02
 */
public class EipOAuthRequestBody extends AbstractEipHttpRequestBody implements Serializable {

    private String clientId;

    private String clientSecret;

    private String appKey;

    private String appSecret;

    private String responseType;

    private String redirectUri;

    private String scope;

    private String state;

    private String prompt;

    private String grantType;

    private String code;

    private String authorization;

    private String accessToken;

    private String refreshToken;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public <NEW extends EipOAuthRequestBody> NEW transferToNewInstance(NEW newBody) {
        newBody.setClientId(this.getClientId());
        newBody.setClientSecret(this.getClientSecret());
        newBody.setAppKey(this.getAppKey());
        newBody.setAppSecret(this.getAppSecret());
        newBody.setResponseType(this.getResponseType());
        newBody.setRedirectUri(this.getRedirectUri());
        newBody.setScope(this.getScope());
        newBody.setState(this.getState());
        newBody.setPrompt(this.getPrompt());
        newBody.setGrantType(this.getGrantType());
        newBody.setCode(this.getCode());
        newBody.setAuthorization(this.getAuthorization());
        newBody.setAccessToken(this.getAccessToken());
        newBody.setRefreshToken(this.getRefreshToken());
        return super.transferToNewInstance(newBody);
    }
}
