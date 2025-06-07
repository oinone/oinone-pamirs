package pro.shushi.pamirs.boot.standard.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataReLoaderApi;
import pro.shushi.pamirs.framework.configure.db.service.MetaService;
import pro.shushi.pamirs.framework.configure.simulate.service.MetaSimulator;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.LogicSchemaService;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.*;
import java.util.function.Consumer;

/**
 * 模块元数据重新加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(66)
@Component
@SPI.Service
public class StandardMetaDataReLoader implements MetaDataReLoaderApi {

    @Override
    public Map<String, MetaData> load(AppLifecycleCommand command, Set<String> loadModules, Consumer<MetaBaseModel> directive) {
        if (CollectionUtils.isEmpty(loadModules)) {
            return new HashMap<>(0);
        }
        boolean reloadMeta = command.getOptions().isReloadMeta();
        if (!reloadMeta) {
            return new HashMap<>(0);
        }
        boolean rebuildTable = command.getOptions().isRebuildTable();
        boolean diffTable = command.getOptions().isDiffTable();
        return TimeWatcher.watch(() -> MetaService.get().loadMetaDataMap(loadModules, directive, () -> {
            if (!rebuildTable) {
                return true;
            }
            // 修正表结构
            List<ModelDefinition> modelDefinitionList = new ArrayList<>();
            for (String model : MetaSimulator.preTable()) {
                ModelDefinition modelDefinition = PamirsSession.getContext().getModelConfig(model).getModelDefinition();
                modelDefinitionList.add(modelDefinition);
            }
            LogicSchemaService.get().buildTable(modelDefinitionList, false, diffTable);
            return true;
        }), "从数据库加载元数据");
    }

    @Override
    public void crossingLoadMetaData(Map<String, MetaData> metaDataMap) {
        MetaService.get().crossingLoadMetaData(metaDataMap);
    }

}
