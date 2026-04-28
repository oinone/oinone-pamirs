package pro.shushi.pamirs.eip.core.manager;

import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.core.common.CommonHttpClientFactory;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmMetricType;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmNotifyStatus;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmNotifyType;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmHistory;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRule;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmStat;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.email.EmailSender;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EipAlarmNotifyManager
 *
 * @author yakir on 2026/04/08 19:06.
 */
@Slf4j
@Component
public class EipAlarmNotifyManager {

    @Autowired
    @Qualifier(CommonHttpClientFactory.HTTP_CLIENTS)
    private CloseableHttpClient httpClient;

    public static final String EIP_ALARM_EMAIL_TEMPLATE = "eip-alarm-email-template";

    public static final String WEBHOOK_TEMPLATE = "{\"msgtype\": \"text\",\"text\": {\"content\":\"%s\"}}";

    public void send(EipAlarmRule rule, EipAlarmStat stat) {

        AlarmNotifyType notifyType = rule.getNotifyType();
        EipAlarmHistory alarmHistory = new EipAlarmHistory();
        Date alartTime = new Date();
        alarmHistory.setAlertTime(alartTime);
        alarmHistory.setInterfaceTechName(stat.getInterfaceTechName());
        alarmHistory.setInterfaceName(stat.getInterfaceName());
        alarmHistory.setRuleName(rule.getName());
        alarmHistory.setRuleTechName(rule.getTechName());
        alarmHistory.setMetricType(rule.getMetricType());

        String windowStart = new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(stat.getStart() * 1000L);
        String windowEnd = new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(stat.getEnd() * 1000L);

        alarmHistory.setTimeWindow(windowStart + " ~ " + windowEnd);
        alarmHistory.setTotalSum(stat.getTotalSum());
        alarmHistory.setSuccessSum(stat.getTotalSum() - stat.getFailSum());
        alarmHistory.setFailSum(stat.getFailSum());
        alarmHistory.setFailSum(stat.getFailSum());

        String failRate = BigDecimal.valueOf(stat.getFailSum())
                .divide(BigDecimal.valueOf(stat.getTotalSum()), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)) + "%";
        alarmHistory.setFailRate(failRate);
        alarmHistory.setNotifyType(rule.getNotifyType());

        String errorMessage = null;
        switch (notifyType) {
            case WEBHOOK:

                errorMessage = notifyWebhook(rule.getWebhookUrl(), rule.getSignSecret(), rule.getNotifyContent());

                break;

            case EMAIL:

                rule.fieldQuery(EipAlarmRule::getReceivers);
                rule.fieldQuery(EipAlarmRule::getEmailTemplate);

                List<PamirsEmployee> receivers = rule.getReceivers();
                if (CollectionUtils.isEmpty(receivers)) {
                    return;
                }

                List<Long> bindingUserIds = new ArrayList<>();
                for (PamirsEmployee employee : receivers) {
                    Long bindingUserId = employee.getBindingUserId();
                    if (null == bindingUserId) {
                        continue;
                    }
                    bindingUserIds.add(bindingUserId);
                }

                IWrapper<PamirsUser> userQw = Pops.<PamirsUser>lambdaQuery()
                        .from(PamirsUser.MODEL_MODEL)
                        .select(
                                PamirsUser::getId,
                                PamirsUser::getEmail,
                                PamirsUser::getContactEmail
                        )
                        .in(PamirsUser::getId, bindingUserIds)
                        .setBatchSize(-1);

                Set<String> emails = Optional.ofNullable(new PamirsUser().queryList(userQw))
                        .map(List::stream)
                        .orElse(Stream.empty())
                        .map(_user -> {
                            if (StringUtils.isNotBlank(_user.getEmail())) {
                                return _user.getEmail();
                            } else if (StringUtils.isNotBlank(_user.getContactEmail())) {
                                return _user.getContactEmail();
                            } else {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                EmailTemplate emailTemplate = rule.getEmailTemplate();

                String timeWindow = formatSeconds(stat.getEnd() - stat.getStart());
                String alarmDesc = "";
                if (AlarmMetricType.FAILURE_COUNT.equals(rule.getMetricType())) {
                    alarmDesc = stat.getFailSum() + I18nUtils.getMessage("pamirs.eip.core.unit.times");

                    alarmHistory.setThreshold(rule.getThreshold() + I18nUtils.getMessage("pamirs.eip.core.unit.times"));
                } else if (AlarmMetricType.FAILURE_RATE.equals(rule.getMetricType())) {
                    alarmDesc = failRate;

                    alarmHistory.setThreshold(rule.getThreshold() * 100 + "%");
                }

                Map<String, Object> bodyParams = new HashMap<>();
                bodyParams.put("interfaceName", stat.getInterfaceName());
                bodyParams.put("interfaceTechName", stat.getInterfaceTechName());
                bodyParams.put("timeWindow", timeWindow);
                bodyParams.put("alarmDesc", alarmDesc);
                bodyParams.put("alarmTime", new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(alartTime));
                bodyParams.put("ruleName", rule.getName());
                bodyParams.put("windowStart", windowStart);
                bodyParams.put("windowEnd", windowEnd);
                bodyParams.put("totalSum", stat.getTotalSum());
                bodyParams.put("successSum", stat.getTotalSum() - stat.getFailSum());
                bodyParams.put("failSum", stat.getFailSum());

                errorMessage = notifyEmail(emailTemplate, emails, bodyParams);

                break;
        }

        if (StringUtils.isBlank(errorMessage)) {
            alarmHistory.setNotifyStatus(AlarmNotifyStatus.SENT);
        } else {
            alarmHistory.setErrorMsg(errorMessage);
            alarmHistory.setNotifyStatus(AlarmNotifyStatus.SEND_FAILED);
        }
        alarmHistory.create();
    }


    public String notifyWebhook(String webhookUrl, String signSecret, String notifyContent) {

        if (StringUtils.isNotBlank(signSecret)) {
            Long timestamp = System.currentTimeMillis();
            String sign = webHookSign(signSecret, timestamp);
            webhookUrl = webhookUrl + "&timestamp=" + timestamp + "&sign=" + sign;
        }
        try {
            String body = String.format(WEBHOOK_TEMPLATE, notifyContent);
            ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.post(webhookUrl)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setEntity(new StringEntity(body, StandardCharsets.UTF_8));
            String response = httpClient.execute(requestBuilder.build(), classicHttpResponse -> {
                byte[] bytes = classicHttpResponse.getEntity().getContent().readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            });
            log.info("Webhook Response:[{}]", response);
            return null;
        } catch (Exception e) {
            log.error("Webhook Notify Error ...", e);
            return transferThrowMessage(e);
        }
    }

    public String notifyEmail(EmailTemplate emailTemplate, Set<String> receiveEmails, Map<String, Object> bodyParams) {

        StringBuilder errorMessages = new StringBuilder();
        try {
            EmailSender emailSender = (EmailSender) MessageEngine.get(MessageEngineTypeEnum.EMAIL_SEND).get(null);
            for (String email : receiveEmails) {
                try {
                    Boolean sendResult = emailSender.send(emailTemplate, bodyParams, email, null);
                    if (!Boolean.TRUE.equals(sendResult)) {
                        log.error("Email Notify Error:{}", JsonUtils.toJSONString(bodyParams));
                        String message = "Email Notify Error：Email" + email + ";\n";
                        errorMessages.append(message);
                    }
                } catch (Exception e) {
                    log.error("Email Notify Error:{}", JsonUtils.toJSONString(bodyParams), e);
                    String errorMsg = transferThrowMessage(e);
                    String message = "Email Notify Error Message: " + errorMsg + " Email：" + email + ";\n";
                    errorMessages.append(message);
                }
            }
        } catch (Throwable throwable) {
            return transferThrowMessage(throwable);
        }
        return errorMessages.toString();
    }

    public String webHookSign(String secret, Long timestamp) {

        String sign = null;
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8.name());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            log.error(I18nUtils.getMessage("pamirs-eip2-core.EipAlarmNotifyManager.sign_error"), e);
        }
        return sign;
    }

    private String transferThrowMessage(Throwable e) {
        String errorMessage = e.getMessage();
        Throwable cause = e.getCause();
        if (cause != null) {
            if (cause instanceof SendFailedException) {
                errorMessage = cause.getMessage();
            } else if (cause instanceof MessagingException) {
                errorMessage = cause.getMessage();
            }
        }
        return errorMessage;
    }

    private String formatSeconds(long totalSeconds) {
        if (totalSeconds < 0) {
            return "";
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append(I18nUtils.getMessage("pamirs.eip.core.time.hour"));
        }
        if (minutes > 0) {
            sb.append(minutes).append(I18nUtils.getMessage("pamirs.eip.core.time.minute"));
        }
        if (seconds > 0 || totalSeconds == 0) {
            sb.append(seconds).append("pamirs.eip.core.time.second");
        }

        return sb.toString();
    }
}
