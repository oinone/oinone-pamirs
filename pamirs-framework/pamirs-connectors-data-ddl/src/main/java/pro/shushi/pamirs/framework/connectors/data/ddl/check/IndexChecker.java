package pro.shushi.pamirs.framework.connectors.data.ddl.check;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModuleIndex;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.CheckUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;

/**
 * 索引校验
 * <p>
 * 2020/6/23 4:32 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class IndexChecker {

    public boolean change(String indexName, LogicTable logicTable, String model) {
        if (null != logicTable.getModelTable()) {
            ModuleIndex moduleIndex = logicTable.getModelTable().getModuleIndexMap().get(indexName);
            // 索引可被修改
            if (ModuleIndex.isEmpty(moduleIndex)) {
                return true;
            } else if (moduleIndex.getModel().equals(model)) {
                return true;
            } else return !Models.inherited().isSameExtendSuperModel(model, moduleIndex.getModel());
        }
        return true;
    }

    public boolean drop(String indexName, LogicTable logicTable, String model) {
        if (null != logicTable.getModelTable()) {
            ModuleIndex moduleIndex = logicTable.getModelTable().getModuleIndexMap().get(indexName);
            // 【重要】手动人为增加的索引，不做默认删除
            // 【重要】手动人为增加的索引，不做默认删除
            if (ModuleIndex.isEmpty(moduleIndex)) {
                return false;
            } else if (moduleIndex.getModel().equals(model)) {
                return true;
            } else if (Models.inherited().isSameExtendSuperModel(model, moduleIndex.getModel())) {
                return false;
            } else if (!CheckUtils.isValidMeta(logicTable.getModule(), moduleIndex.getModule())) {
                return false;
            } else {
                // 修正数据
                moduleIndex.setModule(logicTable.getModule());
                moduleIndex.setModel(logicTable.getModel());
                moduleIndex.setChanged(true);
                return true;
            }
        }
        return true;
    }

}
