package pro.shushi.pamirs.message.engine.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SerializeUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.enmu.*;
import pro.shushi.pamirs.message.model.SmsChannelConfig;
import pro.shushi.pamirs.message.model.SmsTemplate;
import pro.shushi.pamirs.message.model.VerificationCode;
import pro.shushi.pamirs.message.model.VerificationError;
import pro.shushi.pamirs.message.tmodel.SmsTemplateAuditResponse;
import pro.shushi.pamirs.message.tmodel.SmsTemplateResponse;
import pro.shushi.pamirs.message.utils.VerificationCodeUtils;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Order(99)
@Component
@Slf4j
@Ext(SMSSender.class)
public class DefaultSMSSenderExtPoint implements SMSSenderExtPoint {

    private static final String BIZID = "BizId";
    public static final String CODE = "Code";
    public static final String MESSAGE = "Message";
    public static final String OK = "OK";

    @Override
    @ExtPoint.Implement(expression = "smsTemplate.channel.value==\"ALIYUN\"", priority = 999)
    public Boolean send(SmsTemplate smsTemplate, SmsChannelConfig smsChannel, String phoneNum, Map<String, String> placeholders) throws PamirsException {
        return send(smsTemplate, smsChannel, phoneNum, placeholders, false);
    }

    @Override
    @ExtPoint.Implement(expression = "smsTemplate.channel.value==\"ALIYUN\"", priority = 999)
    public Boolean sendOrThrow(SmsTemplate smsTemplate, SmsChannelConfig smsChannel, String phoneNum, Map<String, String> placeholders) throws PamirsException {
        return send(smsTemplate, smsChannel, phoneNum, placeholders, true);
    }

    /**
     * 通过阿里云，发送短信验证码
     *
     * @param smsTemplate  短信模板
     * @param smsChannel   短信通道
     * @param phoneNum     手机号码
     * @param placeholders 占位符
     * @param isThrow      是否抛出异常，为true时会抛出阿里云返回的错误信息
     * @return 短信是否发送成功
     */
    private boolean send(SmsTemplate smsTemplate, SmsChannelConfig smsChannel, String phoneNum, Map<String, String> placeholders, boolean isThrow) throws PamirsException {
        String templateCode = smsTemplate.getTemplateCode();
        SMSTemplateTypeEnum smsTemplateType = smsTemplate.getTemplateType();

        List<VerificationCode> exists = Models.origin().queryListByWrapper(Pops.<VerificationCode>lambdaQuery()
                .from(VerificationCode.MODEL_MODEL)
                .eq(VerificationCode::getIsUsed, false)
                .eq(VerificationCode::getSource, phoneNum)
                .eq(VerificationCode::getSourceType, MessageEngineTypeEnum.SMS_SEND)
                .isNotNull(VerificationCode::getExpirationTime)
                .eq(VerificationCode::getInvalid, Boolean.FALSE)
                .eq(VerificationCode::getVerifyType, smsTemplateType)
                .orderByDesc(VerificationCode::getCreateDate));

        VerificationCode verificationCode = null;
        boolean isNeedCreate = false;
        if (CollectionUtils.isNotEmpty(exists)) {
            verificationCode = exists.get(0);
            if (LocalDateTime.now().isAfter(verificationCode.getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
                isNeedCreate = true;
            }
        } else {
            isNeedCreate = true;
        }

        String code = "";

        String params = VerificationCodeUtils.code(placeholders);
        String outId = phoneNum.concat("-").concat(smsTemplateType.value());
        Map<String, String> paras = new HashMap<>();
        paras.put("PhoneNumbers", phoneNum);
        paras.put("TemplateCode", templateCode);
        paras.put("OutId", outId);

        if (isNeedCreate) {
            if (null == placeholders || !placeholders.containsKey("code")) {
                code = VerificationCodeUtils.code();
                if (placeholders == null) placeholders = new HashMap<>(2);
                placeholders.put("code", code);
            } else {
                code = placeholders.get("code");
            }
            paras.put("TemplateParam", VerificationCodeUtils.code(placeholders));
            if (smsTemplate.getHasVerifyCode() != null && smsTemplate.getHasVerifyCode()) {
                verificationCode = new VerificationCode()
                        .setVerifyType(smsTemplateType)
                        .setSource(phoneNum)
                        .setCode(code)
                        .setParams(params)
                        .setIsUsed(false)
                        .setSourceType(MessageEngineTypeEnum.SMS_SEND)
                        .setExpirationTime(VerificationCodeUtils.plusSec(10 * 60))
                        .setInvalid(false)
                        .setOutId(outId);
                verificationCode = verificationCode.create();
                Models.origin().updateByWrapper(new VerificationError().setErrorNum(0), Pops.<VerificationError>lambdaQuery()
                        .from(VerificationError.MODEL_MODEL)
                        .eq(VerificationError::getVerifyType, smsTemplateType)
                        .eq(VerificationError::getSourceType, MessageEngineTypeEnum.SMS_SEND)
                        .eq(VerificationError::getSource, phoneNum)
                );
            }
        } else {
            if (StringUtils.isBlank(verificationCode.getCode())) {
                code = VerificationCodeUtils.code();
            } else {
                code = verificationCode.getCode();
            }
            if (placeholders == null) placeholders = new HashMap<>(2);
            placeholders.put("code", code);
            params = VerificationCodeUtils.code(placeholders);
            paras.put("TemplateParam", params);
        }


        String responseJson = null;
        try {
            log.info("短信参数{}", JsonUtils.toJSONString(paras));
            responseJson = AliyunSmsHelper.doPost(smsChannel, SMSActionEnum.SEND_SMS, paras);
        } catch (Throwable e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }

        try {
            JsonNode rootNode = SerializeUtils.getObjectMapper().readTree(responseJson);
            if (!checkResult(rootNode, isThrow)) {
                return false;
            }
            if (verificationCode != null) {
                String outBizId = rootNode.get(BIZID).asText();
                verificationCode.setBizId(outBizId)
                        .setExpirationTime(VerificationCodeUtils.plusSec(smsTemplate.getTimeInterval()));
                verificationCode.updateById();
            }
        } catch (JsonProcessingException e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        return true;
    }

    @Override
    public SmsTemplateResponse addTemplate(SmsTemplate smsTemplate, SmsChannelConfig smsChannel) {
        SmsTemplateResponse response = new SmsTemplateResponse();

        String templateCode = smsTemplate.getTemplateCode();
        Integer templateType = getAliyunSmsTemplateType(smsTemplate.getTemplateType());
        String templateName = smsTemplate.getName();
        String templateContent = smsTemplate.getTemplateContent();
        String remark = smsTemplate.getRemark();

        Map<String, String> paras;
        String responseJson;
        if (StringUtils.isNotBlank(templateCode)) {
            //先查询, 如果审核已通过,结束. 未审核通过,编辑
            paras = new HashMap<>();
            paras.put("TemplateCode", templateCode);
            try {
                responseJson = AliyunSmsHelper.doPost(smsChannel, SMSActionEnum.QUERY_SMS_TEMPLATE, paras);
            } catch (Throwable e) {
                throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
            }

            try {
                JsonNode rootNode = SerializeUtils.getObjectMapper().readTree(responseJson);
                if (!checkResult(rootNode, false)) {
                    response.setSuccess(false);
                    response.setErrorMessage(rootNode.get(MESSAGE).asText());
                    return response;
                }
                int templateStatus = rootNode.get("TemplateStatus").asInt();
                SMSTemplateStatusEnum status = AliyunSmsHelper.getApproveStatus(templateStatus);
                if (!SMSTemplateStatusEnum.FAILURE.equals(status)) {
                    //不是审核不通过,不允许修改,结束
                    response.setSuccess(true);
                    response.setTemplateCode(templateCode);
                    //返回服务器上审核通过版本的模板内容
                    response.setTemplateContent(rootNode.get("TemplateContent").asText());
                    response.setStatus(status);
                    return response;
                }
            } catch (JsonProcessingException e) {
                throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
            }
        }
        //新增or更新
        paras = new HashMap<>();
        paras.put("TemplateType", templateType.toString());
        paras.put("TemplateName", templateName);
        paras.put("TemplateContent", templateContent);
        paras.put("Remark", remark);
        SMSActionEnum actionEnum;
        if (StringUtils.isNotBlank(templateCode)) {
            paras.put("TemplateCode", templateCode);
            actionEnum = SMSActionEnum.MODIFY_SMS_TEMPLATE;
        } else {
            actionEnum = SMSActionEnum.ADD_SMS_TEMPLATE;
        }
        try {
            responseJson = AliyunSmsHelper.doPost(smsChannel, actionEnum, paras);
        } catch (Throwable e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        try {
            JsonNode rootNode = SerializeUtils.getObjectMapper().readTree(responseJson);
            if (!checkResult(rootNode, false)) {
                response.setSuccess(false);
                response.setErrorMessage(rootNode.get(MESSAGE).asText());
                return response;
            }
            //成功,新增需要回写code
            response.setSuccess(true);
            response.setTemplateCode(rootNode.get("TemplateCode").asText());
            response.setTemplateContent(templateContent);
            response.setStatus(SMSTemplateStatusEnum.AUDITING);
            return response;
        } catch (JsonProcessingException e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
    }

    @Override
    public SmsTemplateAuditResponse queryTemplate(String templateCode, SmsChannelConfig smsChannel) {
        SmsTemplateAuditResponse response = new SmsTemplateAuditResponse();

        Map<String, String> paras = new HashMap<>();
        String responseJson;
        paras.put("TemplateCode", templateCode);
        try {
            responseJson = AliyunSmsHelper.doPost(smsChannel, SMSActionEnum.QUERY_SMS_TEMPLATE, paras);
        } catch (Throwable e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        try {
            JsonNode rootNode = SerializeUtils.getObjectMapper().readTree(responseJson);
            if (!checkResult(rootNode, false)) {
                response.setSuccess(false);
                response.setErrorMessage(rootNode.get(MESSAGE).asText());
                return response;
            }
            int templateStatus = rootNode.get("TemplateStatus").asInt();
            SMSTemplateStatusEnum status = AliyunSmsHelper.getApproveStatus(templateStatus);
            response.setSuccess(true);
            response.setTemplateCode(templateCode);
            response.setTemplateContent(rootNode.get("TemplateContent").asText());
            response.setStatus(status);
            response.setReason(rootNode.get("Reason").asText());
            return response;
        } catch (JsonProcessingException e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
    }

    private Integer getAliyunSmsTemplateType(SMSTemplateTypeEnum type) {
        if (SMSTemplateTypeEnum.NOTIFY.equals(type)) {
            //短信通知
            return 1;
        }
        //验证码. 其他的阿里短信没对接
        return 0;
    }

    /**
     * 校验阿里云返回的提示信息
     *
     * @param isThrow 是否抛出异常，为true时会抛出阿里云返回的错误信息
     */
    private boolean checkResult(JsonNode rootNode, boolean isThrow) {
        if (!rootNode.has(CODE) || !rootNode.has(MESSAGE)) {
            return false;
        }
        String respCode = rootNode.get(CODE).asText("");
        String respMsg = rootNode.get(MESSAGE).asText("");

        if (StringUtils.equalsIgnoreCase(respCode, OK) && StringUtils.equalsIgnoreCase(respMsg, OK)) {
            return true;
        }
        log.error("阿里云短信请求异常,msg:{}", respMsg);
        if (isThrow) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_SEND_SMS_ERROR).appendMsg(respMsg).errThrow();
        }
        return false;
    }

}
