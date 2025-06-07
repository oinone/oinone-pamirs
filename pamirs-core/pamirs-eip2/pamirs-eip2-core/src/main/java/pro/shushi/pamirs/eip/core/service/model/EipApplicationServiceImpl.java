package pro.shushi.pamirs.eip.core.service.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipAuthentication;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.service.EipOpenRateLimitPolicyService;
import pro.shushi.pamirs.eip.api.service.model.EipApplicationService;
import pro.shushi.pamirs.eip.api.util.EipIpUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Fun(EipApplicationService.FUN_NAMESPACE)
public class EipApplicationServiceImpl implements EipApplicationService {

    @Resource
    private EipOpenRateLimitPolicyService eipOpenRateLimitPolicyService;

    @Override
    @Transactional
    public EipApplication create(EipApplication data) {
        verificationAndSet(data);
        EipApplication result = data.create();
        result.fieldSave(EipApplication::getOpenInterfaceList);
        return result;
    }

    @Override
    @Transactional
    public EipApplication update(EipApplication data) {
        EipApplication exist = data.queryById();
        if (exist == null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NOT_EXIST).errThrow();
        }
        if (data.getOpenInterfaceList() != null) {
            exist.fieldQuery(EipApplication::getOpenInterfaceList);
            Set<String> authorized = exist.getOpenInterfaceList().stream().map(EipOpenInterface::getInterfaceName).collect(Collectors.toSet());
            Set<String> unauthorized = data.getOpenInterfaceList().stream().map(EipOpenInterface::getInterfaceName).collect(Collectors.toSet());

            Collection<String> delete = CollectionUtils.subtract(authorized, unauthorized);
            Collection<String> create = CollectionUtils.subtract(unauthorized, authorized);

            if (CollectionUtils.isNotEmpty(delete)) {
                exist.setOpenInterfaceList(delete.stream().map(i -> (EipOpenInterface) new EipOpenInterface().setInterfaceName(i)).collect(Collectors.toList()))
                        .relationDelete(EipApplication::getOpenInterfaceList);
                // 删除流控配置
                eipOpenRateLimitPolicyService.deleteByInterfaceName(data, new ArrayList<>(delete));
            }
            if (CollectionUtils.isNotEmpty(create)) {
                exist.setOpenInterfaceList(create.stream().map(i -> (EipOpenInterface) new EipOpenInterface().setInterfaceName(i)).collect(Collectors.toList()))
                        .fieldSave(EipApplication::getOpenInterfaceList);
            }
        }

        // 仅更新指定字段
        if (data.getId() != null) {
            String ipWhiteList = data.getIpWhiteList();
            // 校验IP地址是否合法
            if (StringUtils.isNotBlank(ipWhiteList)) {
                EipIpUtil.validateIps(ipWhiteList.split(","));
            }
            EipApplication update = new EipApplication();
            update.setId(data.getId());
            update.setIpWhiteList(data.getIpWhiteList());
            update.setIpWhiteRespHttpCode(data.getIpWhiteRespHttpCode());
            update.setIpWhiteHttpResult(data.getIpWhiteHttpResult());
            update.updateById();
        }
        return data;
    }

    private void verificationAndSet(EipApplication data) {
        String name = data.getName();
        if (StringUtils.isBlank(name)) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NAME_IS_NULL).errThrow();
        }

        EipApplication target = new EipApplication().setName(name).queryOne();
        if (target != null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NAME_EXIST).errThrow();
        }
        String appKey = UUIDUtil.getUUIDNumberString();
        target = new EipApplication().setAppKey(appKey).queryOne();
        while (target != null) {
            appKey = UUIDUtil.getUUIDString();
            target = new EipApplication().setAppKey(appKey).queryOne();
        }
        data.setAppKey(appKey);

        EipAuthentication authentication = Optional.ofNullable(data.getAuthentication()).orElse(new EipAuthentication());
        authentication.setAppKey(appKey);

        EncryptTypeEnum encryptType = authentication.getEncryptType();
        if (encryptType == null) {
            encryptType = EncryptTypeEnum.RSA;
            authentication.setEncryptType(encryptType);
        }
        try {
            String publicKey;
            switch (encryptType) {
                case RSA:
                    KeyPair keyPair = EncryptHelper.getKeyPair(encryptType.getValue(), encryptType.getInitializeSize());
                    publicKey = EncryptHelper.getKey(keyPair.getPublic());
                    authentication.setPrivateKey(EncryptHelper.getKey(keyPair.getPrivate()));
                    authentication.setPublicKey(publicKey);
                    authentication.setAppSecret(EncryptHelper.encryptByKey(keyPair.getPublic(), appKey));
                    break;
                case AES:
                    Key key = EncryptHelper.getKey(encryptType.getValue(), encryptType.getInitializeSize());
                    publicKey = EncryptHelper.getKey(key);
                    authentication.setPrivateKey(publicKey);
                    authentication.setPublicKey(publicKey);
                    authentication.setAppSecret(EncryptHelper.encryptByKey(key, appKey + System.currentTimeMillis()));
                    break;
                default:
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("无法识别的加密类型").errThrow();
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException |
                 IllegalBlockSizeException | IOException e) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR, e).appendMsg("无法生成应用密钥，请稍后再试").errThrow();
        }
        authentication.construct();
        data.setAuthentication(authentication.create());

        // 校验IP白名单是否合法
        String ipWhiteList = data.getIpWhiteList();
        if (StringUtils.isNotBlank(ipWhiteList)) {
            EipIpUtil.validateIps(ipWhiteList.split(","));
        }

        data.construct();
    }

    @Override
    public Boolean enable(EipApplication data) {
        EipApplication target = data.queryById();
        if (target == null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NOT_EXIST).errThrow();
        }
        EipApplication update = new EipApplication();
        update.setId(target.getId());
        update.setDataStatus(DataStatusEnum.ENABLED);
        update.updateById();
        return Boolean.TRUE;
    }

    @Override
    public Boolean disable(EipApplication data) {
        EipApplication target = data.queryById();
        if (target == null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_APPLICATION_NOT_EXIST).errThrow();
        }
        EipApplication update = new EipApplication();
        update.setId(target.getId());
        update.setDataStatus(DataStatusEnum.DISABLED);
        update.updateById();
        return Boolean.TRUE;
    }

    @Override
    public EipApplication queryByAppKey(String appKey) {
        return Models.data().queryOneByWrapper(Pops.<EipApplication>lambdaQuery()
                .from(EipApplication.MODEL_MODEL)
                .eq(EipApplication::getAppKey, appKey)
        );
    }

    @Override
    public List<EipApplication> queryByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }
        return Models.data().queryListByWrapper(Pops.<EipApplication>lambdaQuery()
                .from(EipApplication.MODEL_MODEL)
                .in(EipApplication::getCode, codes));
    }
}
