package pro.shushi.pamirs.sso.api.service;

public interface SsoCommonService {
    void checkAuth(String clientId, String redirectUri, String state);
    String getKey(String username);
}
