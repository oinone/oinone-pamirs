package pro.shushi.pamirs.sso.oauth2.server.model;

import pro.shushi.pamirs.sso.api.model.SsoOauth2ClientDetails;
import java.util.List;

public interface SsoOauth2ClientDetailsService {

    SsoOauth2ClientDetails AutoGenerateClientDetails();

    SsoOauth2ClientDetails getOauth2ClientDetailsInfoByClientId(String clientId);

    List<SsoOauth2ClientDetails> getOauth2ClientDetailsInfos();

    void deleteMultipleOrSingleIds(List<SsoOauth2ClientDetails> ssoOauth2ClientDetailsList);

    Long getOauth2ClientDetailsCount();
}
