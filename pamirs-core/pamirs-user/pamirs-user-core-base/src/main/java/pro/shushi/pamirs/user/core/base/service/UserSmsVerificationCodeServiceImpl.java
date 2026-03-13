package pro.shushi.pamirs.user.core.base.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.sms.SMSSender;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.VerificationCode;
import pro.shushi.pamirs.message.model.VerificationError;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.NumberUtils;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserSmsVerificationCodeService;
import pro.shushi.pamirs.user.api.utils.UserServiceUtils;
import pro.shushi.pamirs.user.core.base.spi.LoginVerificationCodeApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;

/**
 * @author shier
 * date  2022/9/7 10:32 下午
 */
@Slf4j
@Component
public class UserSmsVerificationCodeServiceImpl implements UserSmsVerificationCodeService {

    /**
     * 发送验证码
     *
     * @param userTransient
     */
    @Override
    public void pushPhoneVerificationCode(PamirsUserTransient userTransient, String msgType) {
        if (userTransient.getBroken()) return;
        String phone = userTransient.getPhone();
        String phoneCode = UserServiceUtils.getPhoneCode(userTransient);
        if (StringUtils.isNotBlank(phoneCode)) {
            phone = phoneCode + phone;
        }
        Boolean aBoolean = MessageEngine.<SMSSender>get(MessageEngineTypeEnum.SMS_SEND)
                .get(null)
                .smsSend(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.dictionary, msgType), phone, null);
        if (null == aBoolean || !aBoolean) {
            log.error(USER_SMS_SEND_FAIL_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_SMS_SEND_FAIL_ERROR.msg())
                    .setErrorCode(USER_SMS_SEND_FAIL_ERROR.code())
                    .setErrorField("verificationCode"));
        }
    }

    /**
     * 发送验证码
     *
     * @param userTransient
     */
    @Override
    public void pushPhoneVerificationCodeBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew) {
        if (userTransient.getBroken()) return;
        String phone = isNew == null || !isNew ? userTransient.getPhone() : userTransient.getNewPhone();

        Boolean aBoolean = MessageEngine.<SMSSender>get(MessageEngineTypeEnum.SMS_SEND)
                .get(null)
                .smsSend(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.dictionary, msgType), phone, null);
        if (null == aBoolean || !aBoolean) {
            log.error(USER_SMS_SEND_FAIL_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_SMS_SEND_FAIL_ERROR.msg())
                    .setErrorCode(USER_SMS_SEND_FAIL_ERROR.code())
                    .setErrorField("verificationCode"));
        }
    }


    /**
     * 检查验证码是否正确
     *
     * @param userTransient
     * @return
     */
    @Override
    public void ensureVerificationCode(PamirsUserTransient userTransient, String msgType) {
        ensureVerificationCodeBoth(userTransient, msgType, false, false);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureVerificationCodeBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew, Boolean onlyCheck) {
        if (userTransient.getBroken()) return;
        String verificationCode = isNew == null || !isNew ? userTransient.getVerificationCode() : userTransient.getNewVerificationCode();

        if (StringUtils.isBlank(verificationCode)) {
            log.error("{}", USER_VERIFICATION_CODE_IS_NULL_ERROR.msg());
            broken(userTransient.setErrorCode(USER_VERIFICATION_CODE_IS_NULL_ERROR.code())
                    .setErrorMsg(USER_VERIFICATION_CODE_IS_NULL_ERROR.msg())
                    .setErrorField("verificationCode"));
            return;
        }
        //是否校验验证码
        if (Boolean.TRUE.equals(Spider.getDefaultExtension(LoginVerificationCodeApi.class).checkVerificationCode(verificationCode))) {
            return;
        }
        MessageEngineTypeEnum messageEngineTypeEnum = judgeSourceType(userTransient);
        if (null == messageEngineTypeEnum) return;
        VerificationError error = new VerificationError();
        error.setVerifyType(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.dictionary, msgType));
        error.setSourceType(messageEngineTypeEnum);
        LambdaQueryWrapper<VerificationCode> wrapper = Pops.<VerificationCode>lambdaQuery()
                .from(VerificationCode.MODEL_MODEL)
                .eq(VerificationCode::getVerifyType, SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.dictionary, msgType))
                .eq(VerificationCode::getSourceType, messageEngineTypeEnum)
                .eq(VerificationCode::getCode, verificationCode)
                .isNotNull(VerificationCode::getExpirationTime)
                .eq(VerificationCode::getIsUsed, Boolean.FALSE)
                .eq(VerificationCode::getInvalid, Boolean.FALSE)
                .orderByDesc(VerificationCode::getCreateDate);
        switch (messageEngineTypeEnum) {
            case SMS_SEND:
                String phone = isNew == null || !isNew ? userTransient.getPhone() : userTransient.getNewPhone();
                String phoneCode = UserServiceUtils.getPhoneCode(userTransient);
                if (StringUtils.isNotBlank(phoneCode)) {
                    phone = phoneCode + phone;
                }
                if (StringUtils.isBlank(phone)) {
                    log.error("{}", USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.msg());
                    broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.msg())
                            .setErrorCode(USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.code())
                            .setErrorField("verificationCode"));
                    return;
                }
                wrapper.eq(VerificationCode::getSource, phone);
                error.setSource(phone);
                break;
            case EMAIL_SEND:
                String email = isNew == null || !isNew ? userTransient.getEmail() : userTransient.getNewEmail();
                if (StringUtils.isBlank(email)) {
                    log.error("{}", USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.msg());
                    broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.msg())
                            .setErrorCode(USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.code())
                            .setErrorField("verificationCode"));
                    return;
                }
                wrapper.eq(VerificationCode::getSource, email);
                error.setSource(email);
                break;
        }

        List<VerificationCode> content = new VerificationCode().queryList(wrapper);
        VerificationCode dbCode = null;
        if (CollectionUtils.isNotEmpty(content)) {
            dbCode = content.get(0);
        } else {
            log.error("Verification code not found,{},{}", userTransient.getPhone(), userTransient.getVerificationCode());
            resetCode(error);

            if (NumberUtils.valueOf(error.getErrorNum()) >= 3) {
                broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_EXPIRED_ERROR.msg())
                        .setErrorCode(USER_VERIFICATION_CODE_EXPIRED_ERROR.code())
                        .setErrorField("verificationCode"));
            } else {
                broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_NOT_MATCH_ERROR.msg())
                        .setErrorCode(USER_VERIFICATION_CODE_NOT_MATCH_ERROR.code())
                        .setErrorField("verificationCode"));
            }
            return;
        }

        if (LocalDateTime.now().isAfter(dbCode.getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            log.error("{}", USER_VERIFICATION_CODE_EXPIRED_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_EXPIRED_ERROR.msg())
                    .setErrorCode(USER_VERIFICATION_CODE_EXPIRED_ERROR.code())
                    .setErrorField("verificationCode"));
            return;
        }
        if (onlyCheck == null || !onlyCheck) {
            dbCode.setIsUsed(true);
            dbCode.updateById();
        }
    }

    private String getCode(VerificationCode dbCode) {
        String s = JSON.<HashMap<String, String>>parseObject(dbCode.getCode(), new TypeReference<HashMap<String, String>>() {
        }.getType()).get("code");
        return s == null ? "" : s;
    }

    private MessageEngineTypeEnum judgeSourceType(PamirsUserTransient userTransient) {
        // 以后会有邮箱 发验证码
        return MessageEngineTypeEnum.SMS_SEND;
//        if (StringUtils.isNotBlank(userTransient.getPhone())) {
//            return MailEngineTypeEnum.SMS_SEND;
//        } else if (StringUtils.isNotBlank(userTransient.getPhone())){
//            log.error("{}", USER_PHONE_CODE_NOT_RIGHT_ERROR.msg());
//            broken(userTransient.setErrorMsg(USER_PHONE_CODE_NOT_RIGHT_ERROR.msg())
//                    .setErrorCode(USER_PHONE_CODE_NOT_RIGHT_ERROR.code())
//                    .setErrorField("phone"));
//
//            return null;
//        }
    }

    public void resetCode(VerificationError error) {
        OriginDataManager dataManager = Models.origin();
        VerificationError exist = dataManager.queryOneByWrapper(Pops.<VerificationError>lambdaQuery()
                .from(VerificationError.MODEL_MODEL)
                .eq(VerificationError::getVerifyType, error.getVerifyType())
                .eq(VerificationError::getSourceType, error.getSourceType())
                .eq(VerificationError::getSource, error.getSource()));
        if (null == exist) {
            error.setErrorNum(1);
            error.create();
        } else {
            int errorNum = Optional.ofNullable(exist.getErrorNum()).orElse(0);
            exist.setErrorNum(errorNum + 1);
            if (errorNum >= 2) {
                dataManager.updateByWrapper(new VerificationCode().setInvalid(true), Pops.<VerificationCode>lambdaQuery()
                        .from(VerificationCode.MODEL_MODEL)
                        .eq(VerificationCode::getVerifyType, error.getVerifyType())
                        .eq(VerificationCode::getSourceType, error.getSourceType())
                        .eq(VerificationCode::getSource, error.getSource())
                        .eq(VerificationCode::getIsUsed, false)
                );
                exist.setErrorNum(0);
            }
            error.setErrorNum(exist.getErrorNum());
            error.setId(exist.getId());
            error.updateById();
        }
    }
}
