package pro.shushi.pamirs.draft.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * 草稿配置
 *
 * @author Adamancy Zhang at 15:25 on 2025-10-20
 */
@Configuration
@ConfigurationProperties(prefix = DraftConfiguration.PREFIX)
@Validated
@RefreshScope
public class DraftConfiguration {

    public static final String PREFIX = "pamirs.draft";

    private String storage;

    private StrategyConfig strategy = new StrategyConfig();

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public StrategyConfig getStrategy() {
        return strategy;
    }

    public void setStrategy(StrategyConfig strategy) {
        this.strategy = strategy;
    }

    public static class StrategyConfig {

        private int expire = 7 * 24 * 60 * 60;

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }
    }
}
