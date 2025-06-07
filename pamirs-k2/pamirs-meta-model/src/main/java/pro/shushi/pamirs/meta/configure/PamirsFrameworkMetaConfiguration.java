package pro.shushi.pamirs.meta.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;
import java.util.Map;

/**
 * 框架元数据处理配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-10 01:55
 */
@Configuration
@ConfigurationProperties(
        prefix = PamirsFrameworkMetaConfiguration.PAMIRS_FRAMEWORK_META_CONFIG_PREFIX
)
@RefreshScope
@Data
public class PamirsFrameworkMetaConfiguration {

    public final static String PAMIRS_FRAMEWORK_META_CONFIG_PREFIX = "pamirs.framework.meta";

    private boolean dynamic;

    private Map<String/*module*/, Map<String/*model*/, List<String>/*sign*/>> filter;

}
