package pro.shushi.pamirs.framework.compute.definition.fun;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.definition.fun.FunctionDefinitionManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;

/**
 * 默认函数管理接口实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class DefaultFunctionDefinitionManager implements FunctionDefinitionManager {

    @Override
    public boolean canClientInvoke(ModelConfig modelConfig, Function function) {
        // 处理函数开放级别
        return FunctionOpenEnum.API.in(function.getOpen());
    }

}
