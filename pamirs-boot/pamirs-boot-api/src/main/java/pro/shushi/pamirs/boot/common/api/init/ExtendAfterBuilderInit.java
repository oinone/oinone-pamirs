package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.Map;

/**
 * 扩展构建后置接口
 */
public interface ExtendAfterBuilderInit extends Prioritized, CommonApi {

    /**
     * 扩展构建
     *
     * @param command 生命周期指令
     * @param metaMap 元数据
     * @return 是否构建成功
     */
    boolean init(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap);

}
