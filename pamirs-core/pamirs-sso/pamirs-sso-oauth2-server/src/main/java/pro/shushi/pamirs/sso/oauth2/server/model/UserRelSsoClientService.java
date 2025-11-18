package pro.shushi.pamirs.sso.oauth2.server.model;

import pro.shushi.pamirs.sso.api.model.UserRelSsoClient;

import java.util.List;

public interface UserRelSsoClientService {

    List<UserRelSsoClient> queryListByUserId(Long userId);
}
