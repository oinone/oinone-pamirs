package pro.shushi.pamirs.framework.configure.annotation.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

import static pro.shushi.pamirs.framework.configure.annotation.contants.ConfigureConstants.CONFIGURE_PREFIX;

/**
 * 模型注解转换器配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:17 下午
 */
@Data
@Configuration
@ConfigurationProperties(prefix = CONFIGURE_PREFIX)
@RefreshScope
public class ConfigureSignerConfiguration {

    private List<String> signers;

    private List<String> reflectSigners;

}
