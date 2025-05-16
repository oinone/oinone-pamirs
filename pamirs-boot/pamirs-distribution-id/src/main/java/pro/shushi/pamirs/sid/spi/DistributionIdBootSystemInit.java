package pro.shushi.pamirs.sid.spi;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModelPrepareApi;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootSystemInitApi;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.TableBuilderApi;
import pro.shushi.pamirs.boot.common.util.SystemTableHelper;
import pro.shushi.pamirs.framework.configure.db.service.MetaService;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.LogicSchemaService;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.sid.model.WorkerNode;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动系统后置初始化接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(55)
@Component
@SPI.Service
public class DistributionIdBootSystemInit implements BootSystemInitApi {

    @Resource
    private MetaSimulateService metaSimulateService;

    @Resource
    private SystemTableHelper systemTableHelper;

    @Override
    public void init(AppLifecycleCommand command) {
        boolean diffTable = command.getOptions().isDiffTable();
        boolean rebuildTable = command.getOptions().isRebuildTable();
        if (!rebuildTable) {
            return;
        }
        // 临时构造模块的模型定义
        Map<String/*model*/, String/*simulate model*/> modelMap = new HashMap<>();
        Spider.getDefaultExtension(BootModelPrepareApi.class).prepare(modelMap);
        MetaService.get().prepareModels(modelMap);
        metaSimulateService.transientStaticExecuteWithoutResult(modelMap, () -> {
            ModelDefinition workerNodeDefinition = PamirsSession.getContext().getModelConfig(WorkerNode.MODEL_MODEL).getModelDefinition();
            // 构建基础表结构
            Boolean isSystemTableExist = null;
            if (!isWorkerNodeTableExist(workerNodeDefinition)) {
                isSystemTableExist = systemTableHelper.isSystemTableExist();
                boolean needDiff = diffTable && isSystemTableExist;
                LogicSchemaService.get().buildTable(Lists.newArrayList(workerNodeDefinition), needDiff, needDiff);
            }
            if (!diffTable) {
                return;
            }
            isSystemTableExist = null == isSystemTableExist ? systemTableHelper.isSystemTableExist() : isSystemTableExist;
            if (!isSystemTableExist) {
                Spider.getDefaultExtension(TableBuilderApi.class).buildSys(true);
                LogicSchemaService.get().buildTable(Lists.newArrayList(workerNodeDefinition), true, true);
            }
        });
    }

    private boolean isWorkerNodeTableExist(ModelDefinition workerNodeDefinition) {
        String dsKey = DsApi.get().baseDsKey(workerNodeDefinition.getModel());
        String workerNodeTableName = DataPrefixManager
                .tablePrefix(ModuleConstants.MODULE_BASE, workerNodeDefinition.getModel(), workerNodeDefinition.getTable());
        return Dialects.component(TableMetaDialectService.class, dsKey).existTable(dsKey, workerNodeTableName);
    }

}
