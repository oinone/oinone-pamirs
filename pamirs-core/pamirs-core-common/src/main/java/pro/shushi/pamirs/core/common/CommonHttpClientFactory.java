package pro.shushi.pamirs.core.common;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * CommonHttpClientFactory
 *
 * @author yakir on 2019/08/22 16:43.
 */
@Configuration
public class CommonHttpClientFactory {

    private static final Logger log = LoggerFactory.getLogger(CommonHttpClientFactory.class);

    public static final String HTTP_CLIENTS = "httpClients";

    @Bean(name = HTTP_CLIENTS, destroyMethod = "close")
    public CloseableHttpClient httpClients() throws KeyManagementException, NoSuchAlgorithmException {
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setExpectContinueEnabled(true)
                    .setConnectTimeout(1000 * 60 * 30)
                    .setSocketTimeout(1000 * 60 * 30)
                    .setConnectionRequestTimeout(1000 * 60 * 30)
                    .setCircularRedirectsAllowed(true)
                    .build();

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null,
                    new TrustManager[]{
                            new X509TrustManager() {
                                @Override
                                public X509Certificate[] getAcceptedIssuers() {return null;}

                                @Override
                                public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                                @Override
                                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            }
                    },
                    null);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            cm.setMaxTotal(100); // 最大连接数
            cm.setDefaultMaxPerRoute(100);// 同路由并发数

            return org.apache.http.impl.client.HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setConnectionManager(cm)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException exp) {
            log.error("初始化HTTP连接池出错", exp);
            throw exp;
        }
    }
}