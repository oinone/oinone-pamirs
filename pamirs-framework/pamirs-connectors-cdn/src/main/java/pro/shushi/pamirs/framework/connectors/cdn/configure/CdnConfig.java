package pro.shushi.pamirs.framework.connectors.cdn.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 描述
 * @Author junwei
 * @Date 2020/4/14
 */
@Configuration
@ConfigurationProperties(prefix = CdnConstants.CDN_CONF_PREFIX)
@Data
public class CdnConfig implements Serializable {

    public static final String defaultCdnUrl = "https://pamirs.oss-cn-hangzhou.aliyuncs.com";

    private static final long serialVersionUID = 2893811419928191016L;

    public static final String TYPE = "cdn.oss";

    private String name;

    private String type;

    private String bucket;

    private String uploadUrl;

    private String uploadUrlFormat;

    private String downloadUrl;

    private String downloadUrlFormat;

    private String accessKeyId;

    private String accessKeySecret;

    private String mainDir;

    private String configJson;

    private Long validTime;

    private Long timeout;

    private String callbackUrl;

    private Boolean active;

    private String referer;

    private String localFolderUrl;

    private String backendDownloadUrl;

    /**
     * 图片压缩支持后缀
     */
    private String[] imageResizeExtensions = new String[]{".GIF", ".JPG", ".JPEG", ".BMP", ".DIP", ".JFIF", ".PNG", ".TIF", ".TIFF", ".ICO"};

    /**
     * <h>图片压缩参数（与类型匹配）</h>
     * <p>
     * 指定宽高内的最大图片: m_lfit<br/>
     * 缩略补全: m_pad<br/>
     * 宽: w_xxx<br/>
     * 高: h_xxx<br/>
     * </p>
     */
    private String imageResizeParameter = "m_lfit,h_800";

    /**
     * 跨域支持。典型的华为OBS需要配置跨域支持。多个用逗号分隔
     */
    private String allowedOrigin;

    /**
     * appLogo是否使用CDN的配置
     */
    private Boolean appLogoUseCdn;

    /**
     * 其他CDN配置
     */
    private Map<String, CdnConfig> others = new HashMap<>();
}
