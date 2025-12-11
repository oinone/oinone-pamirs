package pro.shushi.pamirs.sso.client.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpUtils {

    /**
     * get
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    public static HttpResponse doGet(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys) throws Exception {
        HttpClient httpClient = wrapClient(host);
        HttpGet request = new HttpGet(buildUrl(host, path, querys));
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        return httpClient.execute(request);
    }

    /**
     * post form
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param bodys
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, String method,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      Map<String, String> bodys) throws Exception {
        HttpClient httpClient = wrapClient(host);
        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }
        return httpClient.execute(request);
    }

    /**
     * 新增方法：安全的使用方式，返回可关闭的响应
     * 推荐新的调用使用此方法
     */
    public static CloseableHttpResponse doPostSafely(String host, String path, String method,
                                                     Map<String, String> headers,
                                                     Map<String, String> querys,
                                                     String body) throws Exception {
        CloseableHttpClient httpClient = (CloseableHttpClient) wrapClient(host);
        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        CloseableHttpResponse response = null;

        try {
            if (null != headers) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    request.addHeader(e.getKey(), e.getValue());
                }
            }

            if (StringUtils.isNotBlank(body)) {
                request.setEntity(new StringEntity(body, "UTF-8"));
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(180 * 1000)
                    .setConnectionRequestTimeout(180 * 1000)
                    .setSocketTimeout(180 * 1000)
                    .build();
            request.setConfig(requestConfig);

            response = httpClient.execute(request);
            return response;

        } catch (Exception e) {
            closeQuietly(response);
            throw e;
        }
    }

    /**
     * Post String - 修复连接泄漏版本
     * 内部处理资源关闭，保持原有接口兼容性
     *
     * @param host    主机地址
     * @param path    路径
     * @param method  方法（保留参数，实际未使用）
     * @param headers 请求头
     * @param querys  查询参数
     * @param body    请求体
     * @return HttpResponse
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, String method,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      String body) throws Exception {

        CloseableHttpClient httpClient = (CloseableHttpClient) wrapClient(host);
        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        CloseableHttpResponse originalResponse = null;

        try {
            // 设置请求头
            if (null != headers) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    request.addHeader(e.getKey(), e.getValue());
                }
            }
            // 设置请求体
            if (StringUtils.isNotBlank(body)) {
                request.setEntity(new StringEntity(body, "UTF-8"));
            }

            // 设置请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(180 * 1000)
                    .setConnectionRequestTimeout(180 * 1000)
                    .setSocketTimeout(180 * 1000)
                    .build();
            request.setConfig(requestConfig);

            // 执行请求
            originalResponse = httpClient.execute(request);

            // 返回包装的HttpResponse，防止调用方不当关闭
            return new SafeHttpResponseWrapper(originalResponse);

        } catch (Exception e) {
            // 异常时确保关闭响应
            closeQuietly(originalResponse);
            throw e;
        }
    }

    /**
     * 安静地关闭HttpResponse
     */
    private static void closeQuietly(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException ioException) {
            }
        }
    }

    /**
     * 安全包装类，确保连接最终会被关闭
     */
    private static class SafeHttpResponseWrapper implements HttpResponse {
        private final CloseableHttpResponse delegate;
        private volatile boolean closed = false;

        public SafeHttpResponseWrapper(CloseableHttpResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        public void setStatusCode(int code) throws IllegalStateException {
            checkNotClosed();
            delegate.setStatusCode(code);
        }

        @Override
        public StatusLine getStatusLine() {
            checkNotClosed();
            return delegate.getStatusLine();
        }

        @Override
        public void setStatusLine(StatusLine statusline) {
            checkNotClosed();
            delegate.setStatusLine(statusline);
        }

        @Override
        public void setStatusLine(ProtocolVersion ver, int code) {
            checkNotClosed();
            delegate.setStatusLine(ver, code);
        }

        // 添加缺失的 setReasonPhrase 方法
        @Override
        public void setReasonPhrase(String reason) throws IllegalStateException {
            checkNotClosed();
            delegate.setReasonPhrase(reason);
        }

        @Override
        public void setStatusLine(ProtocolVersion ver, int code, String reason) {
            checkNotClosed();
            delegate.setStatusLine(ver, code, reason);
        }

        @Override
        public HttpEntity getEntity() {
            checkNotClosed();
            return delegate.getEntity();
        }

        @Override
        public void setEntity(HttpEntity entity) {
            checkNotClosed();
            delegate.setEntity(entity);
        }

        @Override
        public Locale getLocale() {
            checkNotClosed();
            return delegate.getLocale();
        }

        @Override
        public void setLocale(Locale loc) {
            checkNotClosed();
            delegate.setLocale(loc);
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            checkNotClosed();
            return delegate.getProtocolVersion();
        }

        @Override
        public boolean containsHeader(String name) {
            checkNotClosed();
            return delegate.containsHeader(name);
        }

        @Override
        public Header[] getHeaders(String name) {
            checkNotClosed();
            return delegate.getHeaders(name);
        }

        @Override
        public Header getFirstHeader(String name) {
            checkNotClosed();
            return delegate.getFirstHeader(name);
        }

        @Override
        public Header getLastHeader(String name) {
            checkNotClosed();
            return delegate.getLastHeader(name);
        }

        @Override
        public Header[] getAllHeaders() {
            checkNotClosed();
            return delegate.getAllHeaders();
        }

        @Override
        public void addHeader(Header header) {
            checkNotClosed();
            delegate.addHeader(header);
        }

        @Override
        public void addHeader(String name, String value) {
            checkNotClosed();
            delegate.addHeader(name, value);
        }

        @Override
        public void setHeader(Header header) {
            checkNotClosed();
            delegate.setHeader(header);
        }

        @Override
        public void setHeader(String name, String value) {
            checkNotClosed();
            delegate.setHeader(name, value);
        }

        @Override
        public void setHeaders(Header[] headers) {
            checkNotClosed();
            delegate.setHeaders(headers);
        }

        @Override
        public void removeHeader(Header header) {
            checkNotClosed();
            delegate.removeHeader(header);
        }

        @Override
        public void removeHeaders(String name) {
            checkNotClosed();
            delegate.removeHeaders(name);
        }

        @Override
        public HeaderIterator headerIterator() {
            checkNotClosed();
            return delegate.headerIterator();
        }

        @Override
        public HeaderIterator headerIterator(String name) {
            checkNotClosed();
            return delegate.headerIterator(name);
        }

        @Override
        public HttpParams getParams() {
            checkNotClosed();
            return delegate.getParams();
        }

        @Override
        public void setParams(HttpParams params) {
            checkNotClosed();
            delegate.setParams(params);
        }

        /**
         * 检查是否已关闭
         */
        private void checkNotClosed() {
            if (closed) {
                throw new IllegalStateException("HttpResponse已经被关闭");
            }
        }

        /**
         * 提供关闭方法
         */
        public void close() {
            if (!closed) {
                closed = true;
                closeQuietly(delegate);
            }
        }

        /**
         * toString方法不触发关闭检查
         */
        @Override
        public String toString() {
            if (closed) {
                return "SafeHttpResponseWrapper[closed]";
            }
            return "SafeHttpResponseWrapper[" + delegate.getStatusLine() + "]";
        }

        /**
         * finalize方法作为最后的安全网
         */
        @Override
        protected void finalize() throws Throwable {
            try {
                if (!closed) {
                    close();
                }
            } finally {
                super.finalize();
            }
        }
    }

    /**
     * 工具方法：安全地读取响应内容并自动关闭响应
     */
    public static String readResponseAndClose(HttpResponse response) throws IOException {
        if (response == null) {
            return null;
        }

        try {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }

            // 这里可以使用EntityUtils.toString或其他方式读取内容
            // 注意：实际实现需要根据你的工具类来调整
            return org.apache.http.util.EntityUtils.toString(entity, "UTF-8");

        } finally {
            // 如果response是我们包装的类型，确保关闭
            if (response instanceof SafeHttpResponseWrapper) {
                ((SafeHttpResponseWrapper) response).close();
            }
            // 如果是原生的CloseableHttpResponse，也尝试关闭
            else if (response instanceof CloseableHttpResponse) {
                closeQuietly((CloseableHttpResponse) response);
            }
        }
    }

    /**
     * Post stream
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, String method,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      byte[] body) throws Exception {
        HttpClient httpClient = wrapClient(host);
        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }
        return httpClient.execute(request);
    }

    /**
     * Put String
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                     String body) throws Exception {
        HttpClient httpClient = wrapClient(host);
        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }
        return httpClient.execute(request);
    }

    /**
     * Put stream
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                     byte[] body) throws Exception {
        HttpClient httpClient = wrapClient(host);
        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }
        return httpClient.execute(request);
    }

    /**
     * Delete
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    public static HttpResponse doDelete(String host, String path, String method,
                                        Map<String, String> headers,
                                        Map<String, String> querys) throws Exception {
        HttpClient httpClient = wrapClient(host);
        HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        return httpClient.execute(request);
    }

    private static String buildUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
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

    private static HttpClient wrapClient(String host) {
        HttpClient httpClient = new DefaultHttpClient();
        if (host.startsWith("https://")) {
            sslClient(httpClient);
        }
        return httpClient;
    }

    private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] xcs, String str) {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String str) {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void wireFile(InputStream inputStream, HttpServletResponse response) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
