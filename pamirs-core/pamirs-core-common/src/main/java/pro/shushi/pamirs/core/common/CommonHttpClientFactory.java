package pro.shushi.pamirs.core.common;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.Timeout;
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
import java.util.concurrent.TimeUnit;

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
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setConnectTimeout(Timeout.of(1000 * 60 * 30, TimeUnit.MILLISECONDS))
                    .setSocketTimeout(Timeout.of(1000 * 60 * 30, TimeUnit.MILLISECONDS))
                    .build();

            RequestConfig requestConfig = RequestConfig.custom()
                    .setExpectContinueEnabled(true)
                    .setCircularRedirectsAllowed(true)
                    .setConnectionRequestTimeout(Timeout.of(1000 * 60 * 30, TimeUnit.MILLISECONDS))
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
            cm.setDefaultConnectionConfig(connectionConfig);

            return HttpClients.custom()
                    .setConnectionManager(cm)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException exp) {
            log.error("初始化HTTP连接池出错", exp);
            throw exp;
        }
    }
}