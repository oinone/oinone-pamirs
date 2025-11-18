package pro.shushi.pamirs.sso.oauth2.server.model;

import pro.shushi.pamirs.sso.api.model.SsoClient;

import java.util.List;

public interface SsoClientService {

    SsoClient AutoGenerateClient();

    SsoClient getSsoClientInfoByClientId(String clientId);

    List<SsoClient> getClientInfos();

    void deleteMultipleOrSingleIds(List<SsoClient> ssoClientList);

    Long getOauth2ClientDetailsCount();

    List<SsoClient> queryListByClientIds(List<String> ids);
}
