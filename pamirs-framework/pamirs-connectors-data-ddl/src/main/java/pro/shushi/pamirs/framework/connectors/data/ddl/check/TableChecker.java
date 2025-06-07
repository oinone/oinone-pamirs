package pro.shushi.pamirs.framework.connectors.data.ddl.check;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 模型建表校验
 * <p>
 * 2020/6/23 4:32 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class TableChecker {

    public boolean check(ModelWrapper modelDefinition) {
        // 忽略抽象基类、代理模型和传输模型
        return ModelTypeEnum.STORE.value().equals(modelDefinition.getType());
    }

    public boolean change(ModelWrapper modelDefinition, ModelTable table) {
        if (null == table) {
            return true;
        } else if (modelDefinition.getModel().equals(table.getModel())) {
            return true;
        } else return !Models.inherited().isPropagationExtendInherited(modelDefinition.getModel(), table.getModel());
    }

}
