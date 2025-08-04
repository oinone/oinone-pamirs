package pro.shushi.pamirs.core.common;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.constraints.NotNull;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import pro.shushi.pamirs.core.common.enmu.HttpRequestTypeEnum;
import pro.shushi.pamirs.core.common.http.IgnoredSSLVerifier;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Http请求构建类
 * <p>使用该类可以轻松的发起各种Http请求</p>
 * <p>
 * 该类对JSON格式提供了默认的请求方式
 * 如需使用其他类型的请求方法，可使用<B>addHeader</B>以及<B>addHeaders</B>方法添加自定义的请求头部内容
 * 并且可使用<B>setParams</B>方法添加不同格式的请求参数字符串
 * </p>
 */
public class HttpRequestBuilder {

    /**
     * 请求路径
     */
    private String url;

    /**
     * 请求方式，目前仅支持<B>GET</B>和<B>POST</B>请求方式，默认采用<B>GET</B>方式
     */
    private HttpRequestTypeEnum requestType;

    /**
     * 自定义请求头部内容，如需使用其他类型的头，务必设置<B>Accept</B>和<B>Content-type</B>基本参数，不建议采用全适配的配置方式
     */
    private final Map<String, String> headers;

    /**
     * 请求参数
     * <p>当发起GET请求时，Object对象将默认使用<B>toString</B>方法转化为url中的参数</p>
     * <p>当发起POST请求时，该参数将会使用标准JSON格式字符串自动添加到请求参数中</p>
     * <p>注：当设置了<B>paramsString</B>时，该参数将被任何请求忽略，直接使用<B>paramsString</B>作为请求参数</p>
     */
    private final Map<String, Object> paramsJSON;

    /**
     * 请求参数字符串
     * <p>该参数默认为空字符串，并且在使用<B>setParams</B>方法时自动检查传入参数是否为null，如果为null，则自动使用空字符串进行替代</p>
     * <p>当该值被设置为非空字符串（允许存在仅有空格的情况）时，仅使用该参数作为请求参数，<B>paramsJSON</B>将被忽略</p>
     */
    private String paramsString;

    /**
     * SSL证书上下文
     * 主机名验证
     */
    private SSLConnectionSocketFactoryBuilder connFactoryBuilder;

    /**
     * 禁止直接创建该对象
     *
     * @param url  请求路径
     * @param type 请求方式
     */
    private HttpRequestBuilder(@NotNull String url, @NotNull HttpRequestTypeEnum type) {
        this.url = url;
        this.requestType = type;
        this.headers = new HashMap<>();
        this.paramsJSON = new HashMap<>();
        this.paramsString = CharacterConstants.SEPARATOR_EMPTY;
    }

    /**
     * 构建一个新的HttpHelperBuilder实例对象
     *
     * @param url  请求路径
     * @param type 请求方式
     * @return 新对象
     */
    public static HttpRequestBuilder newInstance(@NotNull String url, @NotNull HttpRequestTypeEnum type) {
        return new HttpRequestBuilder(url, type);
    }

    /**
     * 构建一个新的HttpHelperBuilder实例对象
     *
     * @param url 请求路径
     * @return 新对象
     */
    public static HttpRequestBuilder newInstance(@NotNull String url) {
        return newInstance(url, HttpRequestTypeEnum.GET);
    }

    /**
     * 向请求头部中添加自定义内容
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public HttpRequestBuilder addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * 向请求头部中添加自定义内容
     *
     * @param headers 键-值 Map对象
     * @return 当前对象
     */
    public HttpRequestBuilder addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    /**
     * 向参数列表中添加请求参数内容
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public HttpRequestBuilder addParam(String key, Object value) {
        this.paramsJSON.put(key, value);
        return this;
    }

    /**
     * 向参数列表中添加请求参数内容
     *
     * @param params 键-值 Map对象
     * @return 当前对象
     */
    public HttpRequestBuilder addParams(Map<? extends String, ?> params) {
        this.paramsJSON.putAll(params);
        return this;
    }

    /**
     * 使用参数字符串代替参数列表传参
     *
     * @param paramsString 参数字符串
     * @return 当前对象
     */
    public HttpRequestBuilder setParams(String paramsString) {
        this.paramsString = paramsString == null ? CharacterConstants.SEPARATOR_EMPTY : paramsString;
        return this;
    }

    /**
     * 设置SSL上下文
     *
     * @param sslContext SSL上下文
     * @return 当前对象
     */
    public HttpRequestBuilder setSslContext(SSLContext sslContext) {
        if (null == connFactoryBuilder) {
            connFactoryBuilder = SSLConnectionSocketFactoryBuilder.create();
        }
        connFactoryBuilder.setSslContext(sslContext);
        return this;
    }

    /**
     * 设置主机名验证
     *
     * @param hostnameVerifier 主机名验证
     * @return 当前对象
     */
    public HttpRequestBuilder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        if (null == connFactoryBuilder) {
            connFactoryBuilder = SSLConnectionSocketFactoryBuilder.create();
        }
        connFactoryBuilder.setHostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * 忽略SSL验证
     *
     * @return 当前对象
     */
    public HttpRequestBuilder ignoredSSLVerifier() throws KeyManagementException, NoSuchAlgorithmException {
        if (null == connFactoryBuilder) {
            connFactoryBuilder = SSLConnectionSocketFactoryBuilder.create();
        }
        connFactoryBuilder.setSslContext(IgnoredSSLVerifier.createSSLContext());
        connFactoryBuilder.setHostnameVerifier(IgnoredSSLVerifier.x509HostnameVerifier);
        return this;
    }

    /**
     * 设置请求类型为POST
     *
     * @return 当前对象
     */
    public HttpRequestBuilder POST() {
        this.requestType = HttpRequestTypeEnum.POST;
        return this;
    }

    /**
     * 设置请求类型为GET
     *
     * @return 当前对象
     */
    public HttpRequestBuilder GET() {
        this.requestType = HttpRequestTypeEnum.GET;
        return this;
    }

    /**
     * 发起请求
     *
     * @return 结果字符串
     * @throws IOException 在连接执行过程中可能出现的I/O异常
     */
    public String request() throws IOException {
        String data;
        switch (requestType) {
            case GET:
                data = setHttpRequestUrl(url -> new HttpGet(this.url));
                break;
            case POST:
                HttpPost httpPost = new HttpPost(url);
                data = setHttpRequestEntry(httpPost,
                        request -> httpPost.setEntity(new StringEntity(JSONObject.toJSONString(paramsJSON), StandardCharsets.UTF_8)),
                        request -> httpPost.setEntity(new StringEntity(paramsString, StandardCharsets.UTF_8)));
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(url);
                data = setHttpRequestEntry(httpPut,
                        request -> httpPut.setEntity(new StringEntity(JSONObject.toJSONString(paramsJSON), StandardCharsets.UTF_8)),
                        request -> httpPut.setEntity(new StringEntity(paramsString, StandardCharsets.UTF_8)));
                break;
            case DELETE:
                data = setHttpRequestUrl(url -> new HttpDelete(this.url));
                break;
            default:
                throw new UnsupportedOperationException("Invalid request type. value=" + requestType);
        }
        return data;
    }

    private String setHttpRequestEntry(HttpUriRequest request, Consumer<HttpUriRequest> jsonConsumer, Consumer<HttpUriRequest> stringConsumer) throws IOException {
        if (CharacterConstants.SEPARATOR_EMPTY.equals(paramsString)) {
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json;charset=UTF-8");
            if (jsonConsumer != null) {
                jsonConsumer.accept(request);
            }
        } else {
            if (stringConsumer != null) {
                stringConsumer.accept(request);
            }
        }
        for (String key : headers.keySet()) {
            request.setHeader(key, headers.get(key));
        }
        try (CloseableHttpClient httpClient = generatorClient()) {
            return httpClient.execute(request, new BasicHttpClientResponseHandler());
        }
    }

    private String setHttpRequestUrl(Function<String, HttpUriRequest> processor) throws IOException {
        if (CharacterConstants.SEPARATOR_EMPTY.equals(paramsString)) {
            List<String> parameters = new ArrayList<>();
            for (String key : paramsJSON.keySet()) {
                if (paramsJSON.get(key) != null && !CharacterConstants.SEPARATOR_EMPTY.equals(paramsJSON.get(key))) {
                    parameters.add(String.format("%s=%s", key, paramsJSON.get(key)));
                }
            }
            if (parameters.size() >= 1) {
                url = String.format("%s?%s", url, String.join("&", parameters));
            }
        } else {
            url = url.concat(paramsString);
        }
        HttpUriRequest request = processor.apply(url);
        for (String key : headers.keySet()) {
            request.setHeader(key, headers.get(key));
        }
        try (CloseableHttpClient httpClient = generatorClient()) {
            return httpClient.execute(request, new BasicHttpClientResponseHandler());
        }
    }

    private CloseableHttpClient generatorClient() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (null != connFactoryBuilder) {
            httpClientBuilder.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(connFactoryBuilder.build())
                    .build());
        }
        return httpClientBuilder.build();
    }
}
