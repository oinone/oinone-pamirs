package pro.shushi.pamirs.sso.server.model;

import pro.shushi.pamirs.sso.api.model.SsoClient;

import java.util.List;

public interface SsoClientService {

    SsoClient AutoGenerateClient(SsoClient data);

    SsoClient getByClientId(String clientId);

    List<SsoClient> getClientInfos();

    void deleteMultipleOrSingleIds(List<SsoClient> ssoClientList);

    Long getCount();

    List<SsoClient> queryList(List<String> ids);
}
