package pro.shushi.pamirs.message.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import static pro.shushi.pamirs.core.common.CommonHttpClientFactory.HTTP_CLIENTS;
import static pro.shushi.pamirs.message.enmu.MessageExpEnumerate.SYSTEM_ERROR;


/**
 * @Author: haibo
 * @email: xf.z@shushi.pro
 * @Date: 2019/10/29 5:45 下午
 */
@Slf4j
public class WxWorkUtil {
    private final static String EQ = "=";
    private final static String AND = "&";

    //    @Function
    public static String executePostRequest(String url, Map<String, Object> param) {
        HttpPost postRequest = new HttpPost(url);
        try {
            StringEntity httpEntity = new StringEntity(JSON.toJSONString(param), ContentType.APPLICATION_JSON);
            postRequest.setEntity(httpEntity);
            String rt = BeanDefinitionUtils.getBean(HTTP_CLIENTS, CloseableHttpClient.class).execute(postRequest, new AbstractResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws IOException {
                    return super.handleResponse(response);
                }

                @Override
                public String handleEntity(HttpEntity entity) throws IOException {
                    return EntityUtils.toString(entity, StandardCharsets.UTF_8);
                }
            });
            return rt;
        } catch (IOException e) {
            log.error("{}", SYSTEM_ERROR.msg());
        } finally {
            postRequest.releaseConnection();
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
