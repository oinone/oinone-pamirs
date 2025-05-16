package pro.shushi.pamirs.boot.rpc;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.ExtendAfterBuilderInit;
import pro.shushi.pamirs.framework.faas.spi.api.remote.IRemoteServiceInit;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Map;

/**
 * 远程服务初始化
 * <p>
 * 2020/11/11 10:59 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class ExtendAfterBuilderRemoteInit implements ExtendAfterBuilderInit {

    @Override
    public boolean init(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap) {
        if (!command.getOptions().isPublishService()) {
            return true;
        }
        return Spider.getDefaultExtension(IRemoteServiceInit.class).init(metaMap);
    }

    @Override
    public int priority() {
        return 0;
    }

}
