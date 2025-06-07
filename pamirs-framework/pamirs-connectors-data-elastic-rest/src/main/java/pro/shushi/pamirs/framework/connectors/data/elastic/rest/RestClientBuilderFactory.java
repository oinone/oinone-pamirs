package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.configration.ElasticsearchProperty;
import pro.shushi.pamirs.framework.connectors.data.elastic.rest.http.XProductInterceptor;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * RestClientBuilderFactory
 *
 * @author yakir on 2020/04/13 23:40.
 */
class RestClientBuilderFactory {

    private static final Logger log = LoggerFactory.getLogger(RestClientBuilderFactory.class);

    public static RestClientBuilder builder(final ElasticsearchProperty property) {
        RestClientBuilder.RequestConfigCallback requestConfigCallback =
                (RequestConfig.Builder requestConfigBuilder) -> {
                    requestConfigBuilder.setConnectTimeout(Optional.ofNullable(property.getConnectTimeout()).orElse(60000));
                    requestConfigBuilder.setSocketTimeout(Optional.ofNullable(property.getSocketTimeout()).orElse(60000));
                    requestConfigBuilder.setConnectionRequestTimeout(Optional.ofNullable(property.getConnectTimeout()).orElse(60000));
                    return requestConfigBuilder;
                };

        RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback =
                (HttpAsyncClientBuilder httpClientBuilder) -> {
                    IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                            .setIoThreadCount(Optional.ofNullable(property.getIoThreadCount()).orElse(Runtime.getRuntime().availableProcessors()))
                            .build();
                    SSLContext sslContext = null;
                    try {
                        sslContext = SSLContexts.custom()
                                .loadTrustMaterial(null, (cert, authType) -> true)
                                .build();
                    } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                        throw new RuntimeException(e);
                    }
                    httpClientBuilder.setMaxConnTotal(Optional.ofNullable(property.getMaxConnTotal()).orElse(10));
                    httpClientBuilder.setMaxConnPerRoute(Optional.ofNullable(property.getMaxConnPerRoute()).orElse(10));
                    httpClientBuilder.setDefaultIOReactorConfig(ioReactorConfig);
                    httpClientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
                    httpClientBuilder.setSSLContext(sslContext);
                    httpClientBuilder.addInterceptorLast(new XProductInterceptor());

                    String basicUser = property.getUser();
                    String basicPwd = property.getPassword();
                    if (StringUtils.isNotBlank(basicUser) && StringUtils.isNotBlank(basicPwd)) {
                        log.info("has userName and password");
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(basicUser, basicPwd));
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        httpClientBuilder.disableAuthCaching();
                    }
                    return httpClientBuilder;
                };

        String nodesReplace = Optional.ofNullable(property.getUrl())
                .filter(StringUtils::isNotBlank)
                .map(_nodes -> StringUtils.replace(_nodes, " ", ""))
                .map(_nodes -> StringUtils.replace(_nodes, "\r", ""))
                .map(_nodes -> StringUtils.replace(_nodes, "\n", ""))
                .filter(StringUtils::isNotBlank)
                .orElse("127.0.0.1:9200");

        String schema = (null != property.getUseSSL() && property.getUseSSL()) ? "https" : "http";

        HttpHost[] hosts = Pattern.compile(",").splitAsStream(nodesReplace)
                .map(_node -> _node.split(":"))
                .map(_pair -> new HttpHost(_pair[0], Integer.parseInt(_pair[1]), schema))
                .toArray(HttpHost[]::new);

        return RestClient.builder(hosts)
                .setHttpClientConfigCallback(httpClientConfigCallback)
                .setRequestConfigCallback(requestConfigCallback);
    }
}
