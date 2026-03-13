package pro.shushi.pamirs.user.core.base.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.email.EmailSender;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.message.model.VerificationCode;
import pro.shushi.pamirs.message.model.VerificationError;
import pro.shushi.pamirs.message.utils.VerificationCodeUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.NumberUtils;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserEmailConfirmationService;
import pro.shushi.pamirs.user.core.base.spi.SendEmailServiceApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;

/**
 * @author shier
 * date  2022/9/7 10:32 下午
 */
@Slf4j
@Component
public class UserEmailConfirmationServiceImpl implements UserEmailConfirmationService {

    /**
     * 发送确认邮件
     *
     * @param userTransient
     */
    @Override
    public void sendEmailConfirmation(PamirsUserTransient userTransient, String msgType) {
        sendEmailConfirmationBoth(userTransient, msgType, false);
    }

    @Override
    public void sendEmailConfirmationBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew) {
        if (userTransient.getBroken()) return;

        EmailSender emailSender = MessageEngine.<EmailSender>get(MessageEngineTypeEnum.EMAIL_SEND).get(null);
        EmailTemplate template = null;
        String sendTo = userTransient.getEmail();
        if (isNew) sendTo = userTransient.getNewEmail();

        String code = VerificationCodeUtils.code();
        String outId = sendTo.concat("-").concat(msgType);

        if (UserBehaviorEventEnum.MODIFY_PASSWORD_SEND_RESET_EMAIL.equals(userTransient.getUserBehaviorEvent())) {
            template = new EmailTemplate().setName("修改密码验证码邮件").queryOne();
            userTransient.setVerificationCode(code);
        } else if (UserBehaviorEventEnum.MODIFY_EMAIL_SEND_OLD_EMAIL.equals(userTransient.getUserBehaviorEvent())) {
            template = new EmailTemplate().setName("修改邮箱验证码邮件").queryOne();
            userTransient.setVerificationCode(code);
        } else if (UserBehaviorEventEnum.MODIFY_EMAIL_SEND_NEW_EMAIL.equals(userTransient.getUserBehaviorEvent())) {
            template = new EmailTemplate().setName("设置新邮箱验证码邮件").queryOne();
            userTransient.setNewVerificationCode(code);
        } else if (UserBehaviorEventEnum.MODIFY_PHONE_SEND_EMAIL.equals(userTransient.getUserBehaviorEvent())) {
            template = new EmailTemplate().setName("修改手机号邮件").queryOne();
            userTransient.setVerificationCode(code);

        } else if (UserBehaviorEventEnum.ADD_CORP_SEND_EMAIL_CODE.equals(userTransient.getUserBehaviorEvent())) {
            template = new EmailTemplate().setName("申请加入验证码邮件").queryOne();
            userTransient.setVerificationCode(code);
        } else if (UserBehaviorEventEnum.SEND_LOGIN_BY_EMAIL_CODE.equals(userTransient.getUserBehaviorEvent())) {
            template = new EmailTemplate().setName("登录验证码邮件").queryOne();
            userTransient.setVerificationCode(code);
        } else if (UserBehaviorEventEnum.SIGN_UP_EMAIL.equals(userTransient.getUserBehaviorEvent())) {
            template = new EmailTemplate().setName("邮箱注册邮件").queryOne();
            userTransient.setVerificationCode(code);
        }

        List<VerificationCode> exists = Models.origin().queryListByWrapper(Pops.<VerificationCode>lambdaQuery()
                .from(VerificationCode.MODEL_MODEL)
                .eq(VerificationCode::getIsUsed, false)
                .eq(VerificationCode::getSource, sendTo)
                .eq(VerificationCode::getSourceType, MessageEngineTypeEnum.EMAIL_SEND)
                .isNotNull(VerificationCode::getExpirationTime)
                .eq(VerificationCode::getIsUsed, Boolean.FALSE)
                .eq(VerificationCode::getInvalid, Boolean.FALSE)
                .eq(VerificationCode::getVerifyType, SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.class, msgType))
                .orderByDesc(VerificationCode::getCreateDate));

        VerificationCode verificationCode = new VerificationCode();
        boolean isNeedCreate = false;
        if (CollectionUtils.isNotEmpty(exists)) {
            verificationCode = exists.get(0);
            if (LocalDateTime.now().isAfter(verificationCode.getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
                isNeedCreate = true;
            }
        } else {
            isNeedCreate = true;
        }
        if (isNeedCreate) {
            Map<String, String> params = new HashMap<>(2);
            params.put("realname", Optional.ofNullable(userTransient.getRealname()).orElse(""));
            params.put("code", code);
            params.put("corpname", Optional.ofNullable(userTransient.getRealname()).orElse(""));
            verificationCode.setId(null);
            verificationCode.setVerifyType(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.class, msgType))
                    .setSource(sendTo)
                    .setCode(code)
                    .setParams(VerificationCodeUtils.code(params))
                    .setIsUsed(false)
                    .setSourceType(MessageEngineTypeEnum.EMAIL_SEND)
                    .setInvalid(false)
                    .setExpirationTime(VerificationCodeUtils.plusSec(30 * 60))
                    .setOutId(outId);
            verificationCode.create();

            resetNew(new VerificationError().setSourceType(MessageEngineTypeEnum.EMAIL_SEND).setSource(sendTo).setVerifyType(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.class, msgType)));
        }

        if (template == null) {
            log.error(USER_EMAIL_TEMPLATE_FAIL_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_EMAIL_TEMPLATE_FAIL_ERROR.msg())
                    .setErrorCode(USER_EMAIL_TEMPLATE_FAIL_ERROR.code())
                    .setErrorField("emailConfirmation"));
            return;
        }

        try {
            log.info("Email verification params" + verificationCode.getParams());
            Boolean aBoolean = emailSender.send(template, JsonUtils.parseMap(verificationCode.getParams()), sendTo, null);
            if (null == aBoolean || !aBoolean) {
                log.error(USER_EMAIL_SEND_FAIL_ERROR.msg());
                broken(userTransient.setErrorMsg(USER_EMAIL_SEND_FAIL_ERROR.msg())
                        .setErrorCode(USER_EMAIL_SEND_FAIL_ERROR.code())
                        .setErrorField("emailConfirmation"));
            }
        } catch (Exception e) {
            log.error("Failed to send confirmation email:emailPoster:{},exception:{}", JsonUtils.toJSONString(template), e);
        }
    }

    /**
     * 检查验证码是否正确
     *
     * @param userTransient
     * @return
     */
    @Override
    public void ensureEmailConfirmation(PamirsUserTransient userTransient, String msgType) {
        ensureEmailConfirmationBoth(userTransient, msgType, false, false);
    }

    @Override
    public void sendEmailConfirmationBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew, UserBehaviorEventEnum userBehaviorEventEnum) {
        if (userTransient.getBroken()) return;

        EmailSender emailSender = MessageEngine.<EmailSender>get(MessageEngineTypeEnum.EMAIL_SEND).get(null);

        String sendTo = userTransient.getEmail();
        if (isNew) sendTo = userTransient.getNewEmail();

        String code = VerificationCodeUtils.code();
        String outId = sendTo.concat("-").concat(msgType);

        EmailTemplate template = null;
        List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
        for (SendEmailServiceApi emailServiceApi : sendEmailServiceApis) {
            boolean match = emailServiceApi.match(userBehaviorEventEnum);
            if (match) {
                template = emailServiceApi.getEmailTemplate(userTransient, code);
            }
        }

        List<VerificationCode> exists = Models.origin().queryListByWrapper(Pops.<VerificationCode>lambdaQuery()
                .from(VerificationCode.MODEL_MODEL)
                .eq(VerificationCode::getIsUsed, false)
                .eq(VerificationCode::getSource, sendTo)
                .eq(VerificationCode::getSourceType, MessageEngineTypeEnum.EMAIL_SEND)
                .isNotNull(VerificationCode::getExpirationTime)
                .eq(VerificationCode::getIsUsed, Boolean.FALSE)
                .eq(VerificationCode::getInvalid, Boolean.FALSE)
                .eq(VerificationCode::getVerifyType, SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.class, msgType))
                .orderByDesc(VerificationCode::getCreateDate));

        VerificationCode verificationCode = new VerificationCode();
        boolean isNeedCreate = false;
        if (CollectionUtils.isNotEmpty(exists)) {
            verificationCode = exists.get(0);
            if (LocalDateTime.now().isAfter(verificationCode.getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
                isNeedCreate = true;
            }
        } else {
            isNeedCreate = true;
        }
        if (isNeedCreate) {
            Map<String, String> params = new HashMap<>(2);
            params.put("realname", Optional.ofNullable(userTransient.getRealname()).orElse(""));
            params.put("code", code);
            params.put("corpname", Optional.ofNullable(userTransient.getRealname()).orElse(""));
            verificationCode.setId(null);
            verificationCode.setVerifyType(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.class, msgType))
                    .setSource(sendTo)
                    .setCode(code)
                    .setParams(VerificationCodeUtils.code(params))
                    .setIsUsed(false)
                    .setSourceType(MessageEngineTypeEnum.EMAIL_SEND)
                    .setInvalid(false)
                    .setExpirationTime(VerificationCodeUtils.plusSec(30 * 60))
                    .setOutId(outId);
            verificationCode.create();

            resetNew(new VerificationError().setSourceType(MessageEngineTypeEnum.EMAIL_SEND).setSource(sendTo).setVerifyType(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.class, msgType)));
        }

        if (template == null) {
            log.error(USER_EMAIL_TEMPLATE_FAIL_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_EMAIL_TEMPLATE_FAIL_ERROR.msg())
                    .setErrorCode(USER_EMAIL_TEMPLATE_FAIL_ERROR.code())
                    .setErrorField("emailConfirmation"));
            return;
        }

        try {
            log.info("Email verification params" + verificationCode.getParams());
            Boolean aBoolean = emailSender.send(template, JsonUtils.parseMap(verificationCode.getParams()), sendTo, null);
            if (null == aBoolean || !aBoolean) {
                log.error(USER_EMAIL_SEND_FAIL_ERROR.msg());
                broken(userTransient.setErrorMsg(USER_EMAIL_SEND_FAIL_ERROR.msg())
                        .setErrorCode(USER_EMAIL_SEND_FAIL_ERROR.code())
                        .setErrorField("emailConfirmation"));
            }
        } catch (Exception e) {
            log.error("Failed to send confirmation email:emailPoster:{},exception:{}", JsonUtils.toJSONString(template), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureEmailConfirmationBoth(PamirsUserTransient userTransient, String msgType, Boolean isNew, Boolean onlyCheck) {
        if (userTransient.getBroken()) return;
        String verificationCode = isNew == null || !isNew ? userTransient.getVerificationCode() : userTransient.getNewVerificationCode();

        if (StringUtils.isBlank(verificationCode)) {
            log.error("{}", USER_EMAIL_CONFIRMATION_CODE_IS_NULL_ERROR.msg());
            broken(userTransient.setErrorCode(USER_EMAIL_CONFIRMATION_CODE_IS_NULL_ERROR.code())
                    .setErrorMsg(USER_EMAIL_CONFIRMATION_CODE_IS_NULL_ERROR.msg())
                    .setErrorField("emailConfirmation"));
            return;
        }
        String email = isNew == null || !isNew ? userTransient.getEmail() : userTransient.getNewEmail();
        if (StringUtils.isBlank(email)) {
            log.error("{}", USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.msg())
                    .setErrorCode(USER_VERIFICATION_CODE_HAVE_NO_CORRESPONDING_TYPE_ERROR.code())
                    .setErrorField("verificationCode"));
            return;
        }

        List<VerificationCode> codeList = new VerificationCode().queryList(Pops.<VerificationCode>lambdaQuery()
                .from(VerificationCode.MODEL_MODEL)
                .eq(VerificationCode::getVerifyType, SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.dictionary, msgType))
                .eq(VerificationCode::getSourceType, MessageEngineTypeEnum.EMAIL_SEND)
                .eq(VerificationCode::getCode, verificationCode)
                .eq(VerificationCode::getSource, email)
                .isNotNull(VerificationCode::getExpirationTime)
                .eq(VerificationCode::getIsUsed, Boolean.FALSE)
                .eq(VerificationCode::getInvalid, Boolean.FALSE)
                .orderByDesc(VerificationCode::getCreateDate));

        if (CollectionUtils.isEmpty(codeList)) {
            VerificationError error = new VerificationError();
            error.setVerifyType(SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.dictionary, msgType));
            error.setSourceType(MessageEngineTypeEnum.EMAIL_SEND);
            error.setSource(email);
            resetCode(error);
            if (NumberUtils.valueOf(error.getErrorNum()) >= 3) {
//                log.error("Exception info:"+USER_VERIFICATION_CODE_EXPIRED_ERROR.msg()+JSONUtils.toJSONString(error));
                broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_EXPIRED_ERROR.msg())
                        .setErrorCode(USER_VERIFICATION_CODE_EXPIRED_ERROR.code())
                        .setErrorField("verificationCode"));
            } else {
//                log.error("Exception info:"+USER_VERIFICATION_CODE_NOT_MATCH_ERROR.msg()+JSONUtils.toJSONString(error));
                broken(userTransient.setErrorMsg(USER_VERIFICATION_CODE_NOT_MATCH_ERROR.msg())
                        .setErrorCode(USER_VERIFICATION_CODE_NOT_MATCH_ERROR.code())
                        .setErrorField("verificationCode"));
            }
            return;
        }
        VerificationCode dbCode = codeList.get(0);
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
            if (errorNum >= 2) {
                dataManager.updateByWrapper(new VerificationCode().setInvalid(true), Pops.<VerificationCode>lambdaQuery()
                        .from(VerificationCode.MODEL_MODEL)
                        .eq(VerificationCode::getVerifyType, error.getVerifyType())
                        .eq(VerificationCode::getSourceType, error.getSourceType())
                        .eq(VerificationCode::getSource, error.getSource())
                        .eq(VerificationCode::getIsUsed, false)
                );
            }
            exist.setErrorNum(errorNum + 1);
            error.setErrorNum(exist.getErrorNum());
            error.setId(exist.getId());
            error.updateById();
        }
    }

    public void resetNew(VerificationError error) {
        OriginDataManager dataManager = Models.origin();
        dataManager.updateByWrapper(new VerificationError().setErrorNum(0), Pops.<VerificationError>lambdaQuery()
                .from(VerificationError.MODEL_MODEL)
                .eq(VerificationError::getVerifyType, error.getVerifyType())
                .eq(VerificationError::getSourceType, error.getSourceType())
                .eq(VerificationError::getSource, error.getSource())
        );
    }
}
