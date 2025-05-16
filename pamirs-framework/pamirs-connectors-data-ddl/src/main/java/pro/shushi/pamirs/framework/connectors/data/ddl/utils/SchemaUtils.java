package pro.shushi.pamirs.framework.connectors.data.ddl.utils;

import org.apache.commons.collections4.MapUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.ModelTableContext;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * schema 工具类
 * <p>
 * 2020/8/12 1:50 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SchemaUtils {

    public static String getSchemaTableKey(String schema, String tableName) {
        if (null == schema) {
            schema = CharacterConstants.SEPARATOR_EMPTY;
        }
        if (null == tableName) {
            tableName = CharacterConstants.SEPARATOR_EMPTY;
        }
        return schema + CharacterConstants.SEPARATOR_OCTOTHORPE + tableName;
    }

    public static String getColumnKey(String schema, String tableName, String columnName) {
        if (null == schema) {
            schema = CharacterConstants.SEPARATOR_EMPTY;
        }
        if (null == tableName) {
            tableName = CharacterConstants.SEPARATOR_EMPTY;
        }
        if (null == columnName) {
            columnName = CharacterConstants.SEPARATOR_EMPTY;
        }
        return schema + CharacterConstants.SEPARATOR_OCTOTHORPE + tableName + CharacterConstants.SEPARATOR_OCTOTHORPE + columnName;
    }

    public static String getShardingModel(String model, String sharding) {
        if (null == model) {
            model = CharacterConstants.SEPARATOR_EMPTY;
        }
        if (null == sharding) {
            sharding = CharacterConstants.SEPARATOR_EMPTY;
        }
        return model + CharacterConstants.SEPARATOR_OCTOTHORPE + sharding;
    }

    public static String getModelFromShardingModel(String shardingModel) {
        return shardingModel.split(CharacterConstants.SEPARATOR_OCTOTHORPE)[0];
    }

    public static String getShardingFromShardingModel(String shardingModel) {
        return shardingModel.split(CharacterConstants.SEPARATOR_OCTOTHORPE)[1];
    }

    public static ModelTableContext fetchModelTableContext(Map<String/*schema#table*/, LogicTable> logicTableMap,
                                                           List<ModelDefinition> modelDefinitionList) {
        ModelTableContext modelTableContext = new ModelTableContext();
        modelTableContext.setLogicTableMap(logicTableMap);
        modelTableContext.setModelMap(fetchModelMap(logicTableMap));
        modelTableContext.setExistTableModelMap(fetchExistTableModelMap(modelTableContext.getModelMap(), modelDefinitionList));
        modelTableContext.setMappingLogicTableMap(new HashMap<>());
        return modelTableContext;
    }

    public static Map<String/*model#sharding*/, String/*schema#table*/> fetchModelMap(Map<String/*schema#table*/, LogicTable> logicTableMap) {
        Map<String/*model#sharding*/, String/*schema#table*/> modelMap = new HashMap<>();
        if (!MapUtils.isEmpty(logicTableMap)) {
            for (String key : logicTableMap.keySet()) {
                LogicTable logicTable = logicTableMap.get(key);
                String model = logicTable.getModel();
                if (null == model) {
                    continue;
                }
                modelMap.put(getShardingModel(model, logicTable.getSharding()), key);
            }
        }
        return modelMap;
    }

    public static Map<String/*model*/, String/*model*/> fetchExistTableModelMap(Map<String/*model#sharding*/, String/*schema#table*/> modelMap,
                                                                                List<ModelDefinition> modelDefinitionList) {
        Map<String/*model*/, String/*model*/> existTableModelMap = new HashMap<>();
        if (CollectionUtils.isEmpty(modelDefinitionList) || CollectionUtils.isEmpty(modelMap)) {
            return existTableModelMap;
        }
        Map<String/*dsKey#table*/, String/*model*/> sameTableMap = new HashMap<>();
        for (String shardingModel : modelMap.keySet()) {
            String model = getModelFromShardingModel(shardingModel);
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
            if (null != modelConfig) {
                sameTableMap.put(getSchemaTableKey(modelConfig.getDsKey(), modelConfig.getTable()), model);
            }
        }
        for (ModelDefinition modelDefinition : modelDefinitionList) {
            if (!modelMap.containsKey(modelDefinition.getModel())) {
                String dsKeyTable = getSchemaTableKey(modelDefinition.getDsKey(), modelDefinition.getTable());
                if (sameTableMap.containsKey(dsKeyTable)) {
                    existTableModelMap.put(modelDefinition.getCompletedDsKey(), sameTableMap.get(dsKeyTable));
                }
            }
        }
        return existTableModelMap;
    }

}
