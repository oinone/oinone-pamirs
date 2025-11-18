package pro.shushi.pamirs.sso.oauth2.server.model;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.sso.api.model.UserRelSsoClient;

import java.util.List;

@Service
public class UserRelSsoClientServiceImpl implements UserRelSsoClientService {

    @Override
    public List<UserRelSsoClient> queryListByUserId(Long userId) {
        return new UserRelSsoClient().queryList(
                Pops.<UserRelSsoClient>lambdaQuery()
                        .from(UserRelSsoClient.MODEL_MODEL)
                        .eq(UserRelSsoClient::getUserId, userId)
        );
    }
}
