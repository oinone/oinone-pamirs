package pro.shushi.pamirs.framework.connectors.data.infrastructure.api;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.constant.ModuleFunctionConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 逻辑基础设施接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface LogicSchemaService {

    void initSystemSchema(boolean diffTable);

    void initSystemSchema(String[] models, boolean diffTable);

    default Boolean buildTableWrapper(List<ModelDefinition> modelDefinitions, boolean supportDrop, boolean diffTable) {
        if (CollectionUtils.isEmpty(modelDefinitions)) {
            return Boolean.TRUE;
        }
        pro.shushi.pamirs.meta.api.dto.fun.Function function = PamirsSession.getContext().getFunctionAllowNull(modelDefinitions.get(0).getModule(), ModuleFunctionConstants.FUN_BUILD_TABLE);
        if (function != null) {
            Fun.run(modelDefinitions.get(0).getModule(), ModuleFunctionConstants.FUN_BUILD_TABLE, modelDefinitions, supportDrop, diffTable);
        } else {
            buildTable(modelDefinitions, supportDrop, diffTable);
        }
        return Boolean.TRUE;
    }

    default Boolean dropTableWrapper(ModelDefinition modelDefinition) {
        if (null == modelDefinition || !ModelTypeEnum.STORE.equals(modelDefinition.getType())) {
            return Boolean.FALSE;
        }
        pro.shushi.pamirs.meta.api.dto.fun.Function function = PamirsSession.getContext().getFunctionAllowNull(modelDefinition.getModule(), ModuleFunctionConstants.FUN_DROP_TABLE);
        if (function != null) {
            Fun.run(modelDefinition.getModule(), ModuleFunctionConstants.FUN_DROP_TABLE, modelDefinition);
        } else {
            dropTable(modelDefinition);
        }
        return Boolean.TRUE;
    }

    Boolean dropTable(ModelDefinition modelDefinition);

    Map<String/*module*/, Map<String/*dsKey*/, List<String/*ddl*/>>> buildTable(List<ModelDefinition> modelDefinitions,
                                                                                Boolean supportDrop,
                                                                                Boolean diffTable);

    Map<String/*module*/, Map<String/*dsKey*/, List<String/*ddl*/>>> buildTable(List<ModelDefinition> modelDefinitions,
                                                                                Boolean supportDrop,
                                                                                Boolean diffTable,
                                                                                Function<String, Boolean> autoCreate);

    void buildTable(List<Meta> metaList);

    void buildTable(List<Meta> metaList, Set<String> bootModules);

    void buildTable(List<Meta> metaList,
                    Set<String> bootModules,
                    boolean rebuildTable,
                    boolean diffTable,
                    boolean updateMeta,
                    boolean printDDL);

    void buildTable(List<Meta> metaList,
                    Set<String> bootModules,
                    boolean rebuildTable,
                    boolean diffTable,
                    boolean updateMeta,
                    boolean printDDL,
                    Function<String, Boolean> autoCreate);

    HoldKeeper<LogicSchemaService> holder = new HoldKeeper<>();

    static LogicSchemaService get() {
        return holder.supply(() -> Spider.getDefaultExtension(LogicSchemaService.class));
    }
}
