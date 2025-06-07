package pro.shushi.pamirs.boot.ddl.spi;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.TableBuilderApi;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.LogicSchemaService;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动构建表结构实现
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(66)
@Component
@SPI.Service
public class DdlTableBuilder implements TableBuilderApi {

    @Override
    public void buildSys(boolean diffTable) {

        // 初始化系统表
        LogicSchemaService.get().initSystemSchema(diffTable);

    }

    @Override
    public void build(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap, Set<String> bootModules) {
        boolean rebuildTable = command.getOptions().isRebuildTable();
        boolean printDDL = command.getOptions().isPrintDDL();
        if (!rebuildTable && !printDDL) {
            return;
        }

        if (MapUtils.isEmpty(metaMap)) {
            return;
        }

        // 不是扩展继承，不允许表名相同
        Set<String> completedModuleSet = new HashSet<>();
        List<ModelDefinition> dataList = new ArrayList<>();
        for (Meta meta : metaMap.values()) {
            for (String module : meta.getData().keySet()) {
                if (null != bootModules && !bootModules.contains(module)) {
                    continue;
                }
                if (completedModuleSet.contains(module)) {
                    continue;
                }
                completedModuleSet.add(module);
                List<ModelDefinition> modelList = meta.getData().get(module).getModelList();
                if (!CollectionUtils.isEmpty(modelList)) {
                    dataList.addAll(modelList);
                }
            }
        }
        Map<String, Set<String>> tableModelMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dataList)) {
            for (ModelDefinition modelDefinition : dataList) {
                if (ModelTypeEnum.STORE.equals(modelDefinition.getType())) {
                    if (tableModelMap.containsKey(modelDefinition.getTable())) {
                        tableModelMap.get(modelDefinition.getTable()).add(modelDefinition.getModel());
                    } else {
                        tableModelMap.put(modelDefinition.getTable(), Sets.newHashSet(modelDefinition.getModel()));
                    }
                }
            }
        }
        if (!MapUtils.isEmpty(tableModelMap)) {
            for (Set<String> modelSet : tableModelMap.values()) {
                if (modelSet.size() > 1) {
                    String compareModel = modelSet.iterator().next();
                    for (String model : modelSet) {
                        if (!Models.inherited().isSameExtendSuperModel(compareModel, model)) {
                            throw PamirsException.construct(FwExpEnumerate.BASE_SAME_TABLE_NAME_ERROR)
                                    .appendMsg("model:" + compareModel + ",other:" + model).errThrow();
                        }
                    }
                }
            }
        }

        // 按模块处理业务表
        boolean diffTable = command.getOptions().isDiffTable();
        boolean updateMeta = command.getOptions().isUpdateMeta();
        List<Meta> metaList = metaMap.values().stream()
                .filter(v -> updateMeta || !ModuleConstants.MODULE_BASE.equals(v.getModule())).collect(Collectors.toList());
        LogicSchemaService.get().buildTable(metaList, bootModules, rebuildTable, diffTable, updateMeta, printDDL);
    }
}
