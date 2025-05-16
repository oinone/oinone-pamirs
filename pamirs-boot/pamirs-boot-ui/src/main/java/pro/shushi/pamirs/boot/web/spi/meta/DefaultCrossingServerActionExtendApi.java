package pro.shushi.pamirs.boot.web.spi.meta;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.framework.common.spi.CrossingFunctionExtendApi;
import pro.shushi.pamirs.framework.compare.utils.CrossingComputer;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 跨模块挂载元数据扩展接口默认实现
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service
public class DefaultCrossingServerActionExtendApi implements CrossingFunctionExtendApi {

    public void extend(Meta meta) {
        // 处理跨模块加载的服务器动作
        CrossingComputer.crossingFunCompute(meta, ServerAction.MODEL_MODEL,
                ServerAction::getModel, ServerAction::getModel, ServerAction::getFun);
    }

}
