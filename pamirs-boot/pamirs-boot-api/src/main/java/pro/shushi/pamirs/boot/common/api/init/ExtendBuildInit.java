package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.Map;

/**
 * 扩展构建接口
 *
 * @author Adamancy Zhang
 * date 2020-11-09 16:11
 */
public interface ExtendBuildInit extends Prioritized {

    void build(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap);

}
