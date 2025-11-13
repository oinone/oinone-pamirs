package pro.shushi.pamirs.eip.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

import javax.validation.constraints.NotNull;

/**
 * @author Adamancy Zhang
 * @date 2020-11-05 18:17
 */
@Configuration
@ConfigurationProperties(prefix = EipConfigurationConstant.PAMIRS_EIP_PREFIX)
@Validated
@RefreshScope
@Conditional(EipSwitchCondition.class)
public class PamirsEipProperties {

    @NotNull
    private Boolean enabled = Boolean.TRUE;

    private Http http = new Http();

    /**
     * 定时任务单次同步最大数量
     */
    private Long logCountMaxPageSize = 1000L;

    /**
     * 是否启用日志统计功能
     */
    private Boolean enableLogCount = true;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Http getHttp() {
        return http;
    }

    public void setHttp(Http http) {
        this.http = http;
    }

    public Long getLogCountMaxPageSize() {
        return logCountMaxPageSize;
    }

    public Boolean getEnableLogCount() {
        return enableLogCount;
    }

    public void setEnableLogCount(Boolean enableLogCount) {
        this.enableLogCount = enableLogCount;
    }

    public void setLogCountMaxPageSize(Long logCountMaxPageSize) {
        this.logCountMaxPageSize = logCountMaxPageSize;
    }

    public static class Http {

        private int connectionRequestTimeout = 30000;

        private int connectTimeout = 30000;

        private int socketTimeout = 30000;

        private int maxTotalConnections = 200;

        private int connectionsPerRoute = 20;

        private long connectionTimeToLive = -1;

        public int getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getMaxTotalConnections() {
            return maxTotalConnections;
        }

        public void setMaxTotalConnections(int maxTotalConnections) {
            this.maxTotalConnections = maxTotalConnections;
        }

        public int getConnectionsPerRoute() {
            return connectionsPerRoute;
        }

        public void setConnectionsPerRoute(int connectionsPerRoute) {
            this.connectionsPerRoute = connectionsPerRoute;
        }

        public long getConnectionTimeToLive() {
            return connectionTimeToLive;
        }

        public void setConnectionTimeToLive(long connectionTimeToLive) {
            this.connectionTimeToLive = connectionTimeToLive;
        }
    }
}
