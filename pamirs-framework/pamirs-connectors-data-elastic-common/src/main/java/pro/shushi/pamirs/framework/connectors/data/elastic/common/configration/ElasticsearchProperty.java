package pro.shushi.pamirs.framework.connectors.data.elastic.common.configration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.condition.ElasticsearchCondition;

import java.io.Serializable;

import static pro.shushi.pamirs.framework.connectors.data.elastic.common.constant.Constants.ELASTIC_CFG_PROP_PREFIX;

/**
 * ElasticsearchProperty
 *
 * @author yakir on 2020/04/14 00:15.
 */
@Conditional(ElasticsearchCondition.class)
@Configuration
@ConfigurationProperties(prefix = ELASTIC_CFG_PROP_PREFIX)
public class ElasticsearchProperty implements Serializable {

    private static final long serialVersionUID = -7145182836807336051L;

    private String url;
    private String user;
    private String password;
    private Integer connectTimeout;
    private Integer socketTimeout;
    private Integer requestTimeout;
    private Integer maxConnTotal;
    private Integer maxConnPerRoute;
    private Integer ioThreadCount;
    private String cert;
    private Boolean useSSL;
    private String trustStoreLocation;
    private String trustStoreType; // JKS
    private String trustStorePassword;
    private String keyStoreLocation;
    private String keyStoreType; // JKS
    private String keyStorePassword;
    private Boolean trustSelfSigned;
    private Boolean hostnameVerification;
    private String version;

    public String getUrl() {
        return url;
    }

    public ElasticsearchProperty setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ElasticsearchProperty setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ElasticsearchProperty setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public ElasticsearchProperty setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public ElasticsearchProperty setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public ElasticsearchProperty setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public Integer getMaxConnTotal() {
        return maxConnTotal;
    }

    public ElasticsearchProperty setMaxConnTotal(Integer maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    public Integer getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public ElasticsearchProperty setMaxConnPerRoute(Integer maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    public Integer getIoThreadCount() {
        return ioThreadCount;
    }

    public ElasticsearchProperty setIoThreadCount(Integer ioThreadCount) {
        this.ioThreadCount = ioThreadCount;
        return this;
    }

    public String getCert() {
        return cert;
    }

    public ElasticsearchProperty setCert(String cert) {
        this.cert = cert;
        return this;
    }

    public Boolean getUseSSL() {
        return useSSL;
    }

    public ElasticsearchProperty setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
        return this;
    }

    public String getTrustStoreLocation() {
        return trustStoreLocation;
    }

    public ElasticsearchProperty setTrustStoreLocation(String trustStoreLocation) {
        this.trustStoreLocation = trustStoreLocation;
        return this;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public ElasticsearchProperty setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
        return this;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public ElasticsearchProperty setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    public String getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public ElasticsearchProperty setKeyStoreLocation(String keyStoreLocation) {
        this.keyStoreLocation = keyStoreLocation;
        return this;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public ElasticsearchProperty setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public ElasticsearchProperty setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    public Boolean getTrustSelfSigned() {
        return trustSelfSigned;
    }

    public ElasticsearchProperty setTrustSelfSigned(Boolean trustSelfSigned) {
        this.trustSelfSigned = trustSelfSigned;
        return this;
    }

    public Boolean getHostnameVerification() {
        return hostnameVerification;
    }

    public ElasticsearchProperty setHostnameVerification(Boolean hostnameVerification) {
        this.hostnameVerification = hostnameVerification;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ElasticsearchProperty setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return "ElasticsearchProperty{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", connectTimeout=" + connectTimeout +
                ", socketTimeout=" + socketTimeout +
                ", requestTimeout=" + requestTimeout +
                ", maxConnTotal=" + maxConnTotal +
                ", maxConnPerRoute=" + maxConnPerRoute +
                ", ioThreadCount=" + ioThreadCount +
                ", cert='" + cert + '\'' +
                ", useSSL=" + useSSL +
                ", trustStoreLocation='" + trustStoreLocation + '\'' +
                ", trustStoreType='" + trustStoreType + '\'' +
                ", trustStorePassword='" + trustStorePassword + '\'' +
                ", keyStoreLocation='" + keyStoreLocation + '\'' +
                ", keyStoreType='" + keyStoreType + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", trustSelfSigned=" + trustSelfSigned +
                ", hostnameVerification=" + hostnameVerification +
                ", version='" + version + '\'' +
                '}';
    }
}
