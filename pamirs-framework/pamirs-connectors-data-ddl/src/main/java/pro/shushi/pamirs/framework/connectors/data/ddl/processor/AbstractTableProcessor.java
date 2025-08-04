package pro.shushi.pamirs.framework.connectors.data.ddl.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 表处理抽象基类
 * <p>
 * 2020/6/23 4:32 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public abstract class AbstractTableProcessor {

    @Resource
    private FieldProcessor fieldProcessor;

    public abstract List<String> mainProcess(ModelWrapper modelDefinition, DdlContext ddlContext);

    public List<String> process(ModelWrapper modelDefinition, DdlContext ddlContext) {
        pre(modelDefinition, ddlContext);
        return mainProcess(modelDefinition, ddlContext);
    }

    public void pre(ModelWrapper modelDefinition, DdlContext ddlContext) {
        // 准备模型字段map
        preField(modelDefinition, ddlContext);
        // 逻辑删除字段
        String logicDeleteColumn = fieldProcessor.fetchLogicDeleteColumn(modelDefinition.getModel());
        ddlContext.setLogicDeleteColumn(logicDeleteColumn);
        // 逻辑字段
        Set<String> logicColumns = fieldProcessor.fetchLogicColumns(modelDefinition.getModel());
        ddlContext.setLogicColumns(logicColumns);
        Set<String> deprecatedColumns = fieldProcessor.fetchDeprecatedColumns(ddlContext.getDsKey());
        ddlContext.setDeprecatedColumns(deprecatedColumns);
        // 准备索引
        preIndex(modelDefinition, ddlContext);
    }

    /**
     * 预处理字段
     *
     * @param modelDefinition 模型配置
     * @param ddlContext      ddl上下文
     */
    private void preField(ModelWrapper modelDefinition, DdlContext ddlContext) {
        Map<String/*columnName*/, FieldWrapper> columnFieldMap = new HashMap<>();
        Map<String/*field*/, FieldWrapper> modelFieldMap = new HashMap<>();
        for (FieldWrapper field : modelDefinition.getModelFields()) {
            if (!field.getStore()) {
                continue;
            }
            columnFieldMap.put(field.getColumn(), field);
            modelFieldMap.put(field.getField(), field);
        }
        ddlContext.setColumnFieldMap(columnFieldMap);
        ddlContext.setModelFieldMap(modelFieldMap);
    }

    private void preIndex(ModelWrapper modelDefinition, DdlContext ddlContext) {
        Set<String> indexColumnSet = new LinkedHashSet<>();
        Set<String> uniqueColumnSet = new LinkedHashSet<>();
        // 唯一索引
        List<String> uniques = modelDefinition.getUniques();
        if (!CollectionUtils.isEmpty(uniques)) {
            for (String unique : uniques) {
                // field转column
                String columnUnique = fieldProcessor.fieldConvertColumn(ddlContext.getModelFieldMap(), unique,
                        ddlContext.getLogicColumns(), true);
                if (StringUtils.isNotBlank(columnUnique)) {
                    uniqueColumnSet.add(columnUnique);
                }
            }
        }
        // 索引
        List<String> indexes = modelDefinition.getIndexes();
        if (!CollectionUtils.isEmpty(indexes)) {
            for (String index : indexes) {
                // field转column
                String columnIndex = fieldProcessor.fieldConvertColumn(ddlContext.getModelFieldMap(), index,
                        ddlContext.getLogicColumns(), false);
                if (StringUtils.isNotBlank(columnIndex)) {
                    if (!uniqueColumnSet.contains(columnIndex)) {
                        indexColumnSet.add(columnIndex);
                    }
                }
            }
        }
        ddlContext.setIndexColumnSet(indexColumnSet);
        ddlContext.setUniqueColumnSet(uniqueColumnSet);
    }

}
