package pro.shushi.pamirs.file.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * @Author: Wuer
 * @email: syj@shushi.pro
 * @Date: 2020/8/6 9:24 下午
 * @Description:
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "cdn.oss")
public class FileOSSConfiguration {

    private String name;

    private String type;

    private String bucket;

    private String uploadUrl;

    private String downloadUrl;

    private String accessKeyId;

    private String accessKeySecret;

    private String mainDir;

    private Long validTime;

    private Long timeout;

    private Boolean active;

}
