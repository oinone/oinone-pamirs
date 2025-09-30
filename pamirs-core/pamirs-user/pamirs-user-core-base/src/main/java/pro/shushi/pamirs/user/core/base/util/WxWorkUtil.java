package pro.shushi.pamirs.user.core.base.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.core.common.CommonHttpClientFactory.HTTP_CLIENTS;

/**
 * @Author: haibo
 * @email: xf.z@shushi.pro
 * @Date: 2019/10/29 5:45 下午
 */
@Slf4j
public class WxWorkUtil {
    private final static String EQ = "=";
    private final static String AND = "&";

    public static String executeGetRequest(String url) {
        HttpGet request = new HttpGet(url);
        try {
            String rt = BeanDefinitionUtils.getBean(HTTP_CLIENTS, CloseableHttpClient.class).execute(new HttpGet(url), new BasicHttpClientResponseHandler());
            return rt;
        } catch (IOException e) {
            log.error("{}", UserExpEnumerate.SYSTEM_ERROR.msg(), e);
        }
        return null;
    }

    private static List<NameValuePair> createParam(Map<String, Object> param) {
        List<NameValuePair> nvps = new ArrayList<>();
        if (param != null) {
            for (String k : param.keySet()) {
                nvps.add(new BasicNameValuePair(k, param.get(k).toString()));
            }
        }
        return nvps;
    }

    public static String executePostRequest(String url, Map<String, Object> param) {
        HttpPost postRequest = new HttpPost(url);
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(createParam(param), StandardCharsets.UTF_8);
            postRequest.setEntity(httpEntity);
            String rt = BeanDefinitionUtils.getBean(HTTP_CLIENTS, CloseableHttpClient.class).execute(postRequest, new BasicHttpClientResponseHandler());
            return rt;
        } catch (IOException e) {
            log.error("{}", UserExpEnumerate.SYSTEM_ERROR.msg(), e);
        }
        return null;
    }

    public static String appendParam(String connectString, Map<String, String> param) {
        if (StringUtils.isBlank(connectString) || MapUtils.isEmpty(param)) {
            return connectString;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(connectString).append("?");
        Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            stringBuilder.append(next.getKey()).append(EQ).append(next.getValue());
            if (iterator.hasNext()) {
                stringBuilder.append(AND);
            }
        }
        return stringBuilder.toString();
    }
}
