package pro.shushi.pamirs.boot.common.util;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

import java.text.MessageFormat;

/**
 * 系统表帮助类
 * <p>
 * 2021/12/29 8:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class SystemTableHelper {

    public boolean isSystemTableExist() {
        // 判断系统表是否存在
        String dsKey = DsApi.get().systemDsKey();
        String modelTableName = DataPrefixManager.tablePrefix(ModuleConstants.MODULE_SYSTEM, ModelTable.MODEL_MODEL, ModelTable.TABLE_NAME);
        boolean isExistTable = Dialects.component(TableMetaDialectService.class, dsKey).existTable(dsKey, modelTableName);
        if (!isExistTable) {
            log.debug(MessageFormat.format("System table [{0}] is not exists. DsKey:{1}", modelTableName, dsKey));
        }
        return isExistTable;
    }

}
