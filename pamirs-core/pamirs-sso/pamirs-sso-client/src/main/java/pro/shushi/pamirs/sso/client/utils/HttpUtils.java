package pro.shushi.pamirs.sso.client.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.HttpRequestBuilder;
import pro.shushi.pamirs.core.common.enmu.HttpRequestTypeEnum;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtils {

    /**
     * GET
     * @param url
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    private static String doGet(String url,
                                Map<String, String> headers,
                                Map<String, String> querys) throws Exception {
        HttpRequestBuilder request = HttpRequestBuilder.newInstance(buildUrl(url, querys), HttpRequestTypeEnum.GET);
        if (null != headers) {
            request.addHeaders( headers);
        }
        return request.request();
    }

    public static String doPost(String url,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      Map<String, String> bodys) throws Exception {
        HttpRequestBuilder request = HttpRequestBuilder.newInstance(buildUrl(url, querys), HttpRequestTypeEnum.POST);

        if (null != headers) {
            request.addHeaders( headers);
        }
        if (null != bodys) {
            request.addParams(bodys);
        }
        return request.request();
    }

    private static String buildUrl(String url, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(url);
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }
        return sbUrl.toString();
    }
}
