package pro.shushi.pamirs.message.engine.sms;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import pro.shushi.pamirs.message.enmu.SMSActionEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateStatusEnum;
import pro.shushi.pamirs.message.model.SmsChannelConfig;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static pro.shushi.pamirs.core.common.CommonHttpClientFactory.HTTP_CLIENTS;

/**
 * @author drome
 * @date 2021/8/316:53 下午
 */
@Slf4j
public class AliyunSmsHelper {
    private static final String DFP = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String SIGNATURE = "Signature";

    private static final int SMS_TEMP_APPROVE_AUDITING = 0;
    private static final int SMS_TEMP_APPROVE_SUCCESS = 1;
    private static final int SMS_TEMP_APPROVE_FAILURE = 2;

    public static String doPost(SmsChannelConfig smsChannel, SMSActionEnum actionEnum, Map<String, String> params) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        String httpMethod = HttpPost.METHOD_NAME.toUpperCase();

        SimpleDateFormat df = new SimpleDateFormat(DFP);
        df.setTimeZone(new SimpleTimeZone(0, smsChannel.getTimeZone()));

        params.put("SignatureMethod", smsChannel.getSignatureMethod());
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("AccessKeyId", smsChannel.getAccessKeyId());
        params.put("SignatureVersion", smsChannel.getSignatureVersion());
        params.put("Timestamp", df.format(new Date()));
        params.put("Format", "JSON");
        params.put("Version", smsChannel.getVersion());
        params.put("RegionId", smsChannel.getRegionId());
        params.put("SignName", smsChannel.getSignName());

        params.put("Action", actionEnum.getValue());
        params.remove(SIGNATURE);

        TreeMap<String, String> sortParas = new TreeMap<>(params);
        Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        String endpoint = smsChannel.getEndpoint();

        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(params.get(key)));
        }
        String sortedQueryString = sortQueryStringTmp.substring(1);
        StringBuilder stringToSign = new StringBuilder()
                .append(httpMethod).append("&")
                .append(specialUrlEncode("/")).append("&")
                .append(specialUrlEncode(sortedQueryString));
        String sign = sign(smsChannel.getAccessKeySecret() + "&", stringToSign.toString());
        String signature = specialUrlEncode(sign);
        String requestUrl = endpoint + "/?" + SIGNATURE + "=" + signature + sortQueryStringTmp;
        log.info("SMS Request url: {}", requestUrl);

        HttpPost req = new HttpPost(requestUrl);
        try {
            String responseJson = BeanDefinitionUtils.getBean(HTTP_CLIENTS, CloseableHttpClient.class).execute(req, new BasicResponseHandler());
            log.info("Response: {}", responseJson);
            return responseJson;
        }catch (Throwable throwable){
            log.error("请求发送短信失败", throwable);
            throw throwable;
        } finally {
            req.releaseConnection();
        }
    }

    public static SMSTemplateStatusEnum getApproveStatus(int aliyunStatus){
        switch (aliyunStatus){
            case SMS_TEMP_APPROVE_AUDITING:
                return SMSTemplateStatusEnum.AUDITING;
            case SMS_TEMP_APPROVE_SUCCESS:
                return SMSTemplateStatusEnum.SUCCESS;
            case SMS_TEMP_APPROVE_FAILURE:
                return SMSTemplateStatusEnum.FAILURE;
        }
        return null;
    }


    private static String specialUrlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~")
                ;
    }

    private static String sign(String accessSecret, String stringToSign) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(signData), StandardCharsets.UTF_8);
    }
}
