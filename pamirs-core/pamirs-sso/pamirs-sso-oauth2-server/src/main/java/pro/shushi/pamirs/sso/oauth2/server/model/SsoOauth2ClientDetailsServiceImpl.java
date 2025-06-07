package pro.shushi.pamirs.sso.oauth2.server.model;

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
import pro.shushi.pamirs.sso.api.model.SsoOauth2ClientDetails;
import pro.shushi.pamirs.sso.api.utils.EncryptionHandler;
import pro.shushi.pamirs.user.api.utils.CookieUtil;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SsoOauth2ClientDetailsServiceImpl implements SsoOauth2ClientDetailsService {

    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @Override
    public SsoOauth2ClientDetails AutoGenerateClientDetails() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String accessToken = CookieUtil.getValue(request, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
        String clientIdAndOpenId = JwtTokenUtil.getKeyFromToken(accessToken);
        if (StringUtils.isEmpty(clientIdAndOpenId)) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_CREATE_CLIENT_ERROR).errThrow();
        }
        SsoOauth2ClientDetails ssoOauth2ClientDetails = new SsoOauth2ClientDetails();
        String clientId = EncryptionHandler.generateClientId();
        try {
            Map<String, String> result = EncryptionHandler.generateClientSecret(clientId);
            if (MapUtils.isEmpty(result)) {
                throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_MAP_KEY_SECRET_ERROR).errThrow();
            }
            ssoOauth2ClientDetails.setClientId(clientId);
            ssoOauth2ClientDetails.setClientSecret(result.get(SsoConfigurationConstant.PAMIRS_SSO_CLIENT_PUBLIC));
            ssoOauth2ClientDetails.setEnabled(pamirsSsoProperties.getEnabled());
            ssoOauth2ClientDetails.setExpiresIn(pamirsSsoProperties.getServer().getDefaultExpires().getExpiresIn());
            ssoOauth2ClientDetails.setRefreshTokenExpiresIn(pamirsSsoProperties.getServer().getDefaultExpires().getRefreshTokenExpiresIn());
            ssoOauth2ClientDetails.setCodeExpiresIn(pamirsSsoProperties.getServer().getDefaultExpires().getCodeExpiresIn());
            ssoOauth2ClientDetails.setPrivateKey(result.get(SsoConfigurationConstant.PAMIRS_SSO_INTERNAL_CLIENT_PRIVATE));
            ssoOauth2ClientDetails.setPublicKey(result.get(SsoConfigurationConstant.PAMIRS_SSO_INTERNAL_CLIENT_PUBLIC));
            ssoOauth2ClientDetails.setCacheTokenExpirationTime(pamirsSsoProperties.getServer().getDefaultExpires().getCacheTokenExpirationTime());
            SsoOauth2ClientDetails oauth2ClientDetail = ssoOauth2ClientDetails.create();
        } catch (Exception e) {
            throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_CREATE_CLIENT_ERROR).errThrow();
        }

        return ssoOauth2ClientDetails;
    }

    @Override
    public SsoOauth2ClientDetails getOauth2ClientDetailsInfoByClientId(String clientId) {
        LambdaQueryWrapper<SsoOauth2ClientDetails> qw = Pops.<SsoOauth2ClientDetails>lambdaQuery()
                .from(SsoOauth2ClientDetails.MODEL_MODEL)
                .eq(SsoOauth2ClientDetails::getEnabled, Boolean.TRUE)
                .eq(SsoOauth2ClientDetails::getClientId, clientId);
        return new SsoOauth2ClientDetails().queryOneByWrapper(qw);
    }

    @Override
    public List<SsoOauth2ClientDetails> getOauth2ClientDetailsInfos() {
        return new SsoOauth2ClientDetails().queryList();
    }

    @Override
    public void deleteMultipleOrSingleIds(List<SsoOauth2ClientDetails> ssoOauth2ClientDetailsList) {
        List<String> clientIds = ssoOauth2ClientDetailsList.stream().map(item -> item.getClientId()).collect(Collectors.toList());
        LambdaQueryWrapper<SsoOauth2ClientDetails> qw = Pops.<SsoOauth2ClientDetails>lambdaQuery().in(SsoOauth2ClientDetails::getClientId, clientIds).from(SsoOauth2ClientDetails.MODEL_MODEL);
        new SsoOauth2ClientDetails().deleteByWrapper(qw);
    }

    @Override
    public Long getOauth2ClientDetailsCount() {
        return new SsoOauth2ClientDetails().count();
    }
}
