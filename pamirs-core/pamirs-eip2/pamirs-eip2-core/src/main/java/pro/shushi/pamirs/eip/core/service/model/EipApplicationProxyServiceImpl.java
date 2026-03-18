package pro.shushi.pamirs.eip.core.service.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipAuthentication;
import pro.shushi.pamirs.eip.api.pmodel.EipApplicationProxy;
import pro.shushi.pamirs.eip.api.service.model.EipApplicationProxyService;
import pro.shushi.pamirs.eip.api.service.model.EipApplicationService;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;

@Component
public class EipApplicationProxyServiceImpl implements EipApplicationProxyService {

    @Autowired
    private EipApplicationService eipApplicationService;

    @Override
    public <T extends EipApplicationProxy> T create(T data) {
        data.setAuthentication(
                new EipAuthentication().setEncryptType(data.getEncryptType())
        );
        EipApplication result = eipApplicationService.create(data);

        data.setId(result.getId());
        return queryById(data);
    }

    @Override
    public <T extends EipApplicationProxy> T update(T data) {
        EipApplication result = eipApplicationService.update(data);

        data.setId(result.getId());
        return queryById(data);
    }

    @Override
    public <T extends EipApplicationProxy> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        Pagination<T> result = Models.origin().queryPage(page, queryWrapper);
        List<T> resultList = result.getContent();
        if (CollectionUtils.isEmpty(resultList)) {
            return result;
        }
        outConvert(resultList, Boolean.FALSE);
        return result;
    }

    @Override
    public <T extends EipApplicationProxy> T queryOne(T query) {
        T result = query.queryOne();
        if (result == null) {
            return result;
        }
        outConvert(Collections.singletonList(result), Boolean.TRUE);
        return result;
    }

    public <T extends EipApplicationProxy> T queryById(T query) {
        T result = query.queryById();
        if (result == null) {
            return result;
        }
        outConvert(Collections.singletonList(result), Boolean.TRUE);
        return result;
    }

    private <T extends EipApplicationProxy> void outConvert(List<T> applicationProxyList, Boolean sensitive) {
        if (CollectionUtils.isEmpty(applicationProxyList)) {
            return;
        }
        Models.data().listFieldQuery(applicationProxyList, EipApplicationProxy::getAuthentication);
        for (EipApplicationProxy eipApplicationProxy : applicationProxyList) {
            EipAuthentication authentication = eipApplicationProxy.getAuthentication();
            if (authentication != null) {
                eipApplicationProxy.setEncryptType(authentication.getEncryptType());
                eipApplicationProxy.setEncryptKeyType(
                        EncryptTypeEnum.AES.equals(eipApplicationProxy.getEncryptType()) ? "对称密钥" : "公钥"
                );
                if (Boolean.TRUE.equals(sensitive)) {
                    eipApplicationProxy.setAppSecret(authentication.getAppSecret());
                    eipApplicationProxy.setPublicKey(authentication.getPublicKey());
                }

                //置空敏感信息
                authentication.unsetAppSecret();
                authentication.unsetPublicKey();
            }
        }
    }

    @Override
    public <T extends EipApplicationProxy> T regenerateSecret(T data) {
        String appKey = data.getAppKey();
        if (StringUtils.isBlank(appKey)) {
            throw PamirsException.construct(EipExpEnumerate.INVALID_APPLICATION_KEY_ERROR).errThrow();
        }
        EipApplicationProxy eipApplication = new EipApplicationProxy().setAppKey(appKey).queryOne();
        if (eipApplication == null) {
            throw PamirsException.construct(EipExpEnumerate.INVALID_APPLICATION_KEY_ERROR).errThrow();
        }
        EipAuthentication authentication = eipApplication.fieldQuery(EipApplicationProxy::getAuthentication).getAuthentication();
        if (authentication == null) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(I18nUtils.getMessage("pamirs.eip.application.auth.error")).errThrow();
        }
        EncryptTypeEnum encryptType = authentication.getEncryptType();
        if (encryptType == null) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(I18nUtils.getMessage("pamirs.eip.application.encrypt.unknown")).errThrow();
        }
        try {
            String secretValue = appKey + System.currentTimeMillis();
            switch (encryptType) {
                case RSA:
                    authentication.setAppSecret(EncryptHelper.encryptByKey(EncryptHelper.getPublicKey(encryptType.getValue(), authentication.getPublicKey()), secretValue));
                    break;
                case AES:
                    authentication.setAppSecret(EncryptHelper.encryptByKey(EncryptHelper.getSecretKeySpec(encryptType.getValue(), authentication.getPublicKey()), secretValue));
                    break;
                default:
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(I18nUtils.getMessage("pamirs.eip.application.encrypt.unknown")).errThrow();
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException | IOException e) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR, e).appendMsg(I18nUtils.getMessage("pamirs.eip.application.secret.refresh.failed")).errThrow();
        }
        authentication.updateById();
        authentication.unsetPrivateKey();
        data.setAuthentication(authentication);
        return data;
    }

    @Override
    public <T extends EipApplicationProxy> T dataStatusEnable(T data) {
        eipApplicationService.enable(data);
        return queryById(data);
    }

    @Override
    public <T extends EipApplicationProxy> T dataStatusDisable(T data) {
        eipApplicationService.disable(data);
        return queryById(data);
    }
}
