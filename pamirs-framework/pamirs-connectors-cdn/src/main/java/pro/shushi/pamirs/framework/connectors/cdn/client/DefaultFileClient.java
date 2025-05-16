package pro.shushi.pamirs.framework.connectors.cdn.client;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

/**
 * 未配置文件系统的情况, 解决系统图标问题
 */
@Slf4j
@Component
public class DefaultFileClient extends AbstractFileClient implements FileConstants {

    @Override
    public CdnFileForm getFormData(String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStaticUrl() {
        return CdnConfig.defaultCdnUrl;
    }

}

