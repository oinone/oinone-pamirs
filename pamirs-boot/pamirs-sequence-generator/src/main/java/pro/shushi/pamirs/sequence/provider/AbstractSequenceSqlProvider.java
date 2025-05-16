package pro.shushi.pamirs.sequence.provider;

import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.sequence.model.LeafAlloc;

/**
 * sql生成器
 * <p>
 * 2021/9/23 1:24 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AbstractSequenceSqlProvider {

    protected static String fetchTable() {
        return DataPrefixManager.tablePrefix(ModuleConstants.MODULE_BASE, LeafAlloc.MODEL_MODEL, "leaf_alloc");
    }

}
