package pro.shushi.pamirs.sso.oauth2.server.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;
import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.user.api.utils.CookieUtil;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SsoClientServiceImpl implements SsoClientService {

    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @Override
    public SsoClient AutoGenerateClient() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String accessToken = CookieUtil.getValue(request, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
        String clientIdAndOpenId = JwtTokenUtil.getKeyFromToken(accessToken);
        if (StringUtils.isEmpty(clientIdAndOpenId)) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_CREATE_CLIENT_ERROR).errThrow();
        }
        SsoClient ssoClient = new SsoClient();
        String clientId = EncryptionHandler.generateClientId();
        try {
            Map<String, String> result = EncryptionHandler.generateClientSecret(clientId);
            if (MapUtils.isEmpty(result)) {
                throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_MAP_KEY_SECRET_ERROR).errThrow();
            }
            ssoClient.setClientId(clientId);
            ssoClient.setClientSecret(result.get(SsoConfigurationConstant.PAMIRS_SSO_CLIENT_PUBLIC));
            ssoClient.setEnabled(pamirsSsoProperties.getEnabled());
            ssoClient.setExpiresIn(pamirsSsoProperties.getServer().getDefaultExpires().getExpiresIn());
            ssoClient.setRefreshTokenExpiresIn(pamirsSsoProperties.getServer().getDefaultExpires().getRefreshTokenExpiresIn());
            ssoClient.setCodeExpiresIn(pamirsSsoProperties.getServer().getDefaultExpires().getCodeExpiresIn());
            ssoClient.setPrivateKey(result.get(SsoConfigurationConstant.PAMIRS_SSO_INTERNAL_CLIENT_PRIVATE));
            ssoClient.setPublicKey(result.get(SsoConfigurationConstant.PAMIRS_SSO_INTERNAL_CLIENT_PUBLIC));
            ssoClient.setCacheTokenExpirationTime(pamirsSsoProperties.getServer().getDefaultExpires().getCacheTokenExpirationTime());
            SsoClient oauth2ClientDetail = ssoClient.create();
        } catch (Exception e) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_CREATE_CLIENT_ERROR).errThrow();
        }

        return ssoClient;
    }

    @Override
    public SsoClient getSsoClientInfoByClientId(String clientId) {
        LambdaQueryWrapper<SsoClient> qw = Pops.<SsoClient>lambdaQuery()
                .from(SsoClient.MODEL_MODEL)
                .eq(SsoClient::getEnabled, Boolean.TRUE)
                .eq(SsoClient::getClientId, clientId);
        return new SsoClient().queryOneByWrapper(qw);
    }

    @Override
    public List<SsoClient> getClientInfos() {
        return new SsoClient().queryList();
    }

    @Override
    public void deleteMultipleOrSingleIds(List<SsoClient> ssoClientList) {
        List<String> clientIds = ssoClientList.stream().map(item -> item.getClientId()).collect(Collectors.toList());
        LambdaQueryWrapper<SsoClient> qw = Pops.<SsoClient>lambdaQuery().in(SsoClient::getClientId, clientIds).from(SsoClient.MODEL_MODEL);
        new SsoClient().deleteByWrapper(qw);
    }

    @Override
    public Long getOauth2ClientDetailsCount() {
        return new SsoClient().count();
    }

    @Override
    public List<SsoClient> queryListByClientIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>(1);
        }
        return new SsoClient().queryList(
                Pops.<SsoClient>lambdaQuery()
                        .from(SsoClient.MODEL_MODEL)
                        .eq(SsoClient::getEnabled, Boolean.TRUE)
                        .in(SsoClient::getClientId, ids)
        );
    }
}
