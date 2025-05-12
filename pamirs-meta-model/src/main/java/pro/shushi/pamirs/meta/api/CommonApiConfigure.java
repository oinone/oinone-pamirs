package pro.shushi.pamirs.meta.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 框架接口配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/5 1:53 上午
 */
@Configuration
@ConfigurationProperties(prefix = "pamirs.api")
@RefreshScope
public class CommonApiConfigure {

    private Map<String/*接口全限定名*/, String/*启用bean的名称*/> apiMap = new ConcurrentHashMap<>();

    public Map<String, String> getApiMap() {
        return apiMap;
    }

    public void setApiMap(Map<String, String> apiMap) {
        this.apiMap = apiMap;
    }

}
