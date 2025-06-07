package pro.shushi.pamirs.framework.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.List;

/**
 * 元数据加载配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@Data
@Configuration
@ConfigurationProperties(prefix = ConfigureConstants.PAMIRS_META_CONFIG_PREFIX)
@RefreshScope
public class MetaConfiguration {

    public static final String DEFAULT_VIEWS_PACKAGE = "/pamirs/views";

    private List<String> metaPackages;

    private String viewsPackage = DEFAULT_VIEWS_PACKAGE;

    private MetaRelationConfigModel relation;

    private InformationLevelEnum logLevel = InformationLevelEnum.ERROR;

}
