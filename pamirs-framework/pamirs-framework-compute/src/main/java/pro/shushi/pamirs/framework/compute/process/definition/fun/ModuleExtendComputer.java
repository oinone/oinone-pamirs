package pro.shushi.pamirs.framework.compute.process.definition.fun;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.compute.definition.MetaDataExtendComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import javax.annotation.Resource;

/**
 * 扩展点元数据计算逻辑扩展
 * <p>
 * 2020/4/26 11:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order(1)
@Component
public class ModuleExtendComputer implements MetaDataExtendComputer<ModuleDefinition> {

    @Resource
    private InheritedProcessor inheritedProcessor;

    @Override
    public Result<Void> compute(Meta meta, String model, ModuleDefinition data) {
        if (null != data) {
            data.unsetState();
        }
        return new Result<>();
    }

    @Override
    public boolean canCompute(String model) {
        return ModuleDefinition.MODEL_MODEL.equals(model);
    }

}
