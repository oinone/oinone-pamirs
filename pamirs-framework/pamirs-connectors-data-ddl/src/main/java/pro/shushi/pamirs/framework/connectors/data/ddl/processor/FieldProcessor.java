package pro.shushi.pamirs.framework.connectors.data.ddl.processor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.ColumnComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.IndexComponent;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import javax.annotation.Resource;
import java.util.*;

/**
 * 数据表字段配置计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class FieldProcessor {

    @Resource
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    @Resource
    private ColumnComponent columnComponent;

    @Resource
    private IndexComponent indexComponent;

    public FieldWrapper fetchModelField(ModelWrapper modelDefinition, String field) {
        List<FieldWrapper> modelFieldList = Optional.ofNullable(modelDefinition).map(ModelWrapper::getModelFields).orElse(null);
        if (CollectionUtils.isEmpty(modelFieldList)) {
            return null;
        }
        return modelFieldList.stream().filter(s -> field.equals(s.getField())).findFirst().orElse(null);
    }

    public String prepareModelFields(String dsKey, ModelWrapper modelDefinition, FieldWrapper modelField) {
        modelField.setIsPrimaryKey(CollectionUtils.isNotEmpty(modelDefinition.getPk())
                && modelDefinition.getPk().contains(modelField.getField()));
        String columnDefinition = columnComponent.columnDefinition(dsKey, modelField).toUpperCase();
        columnDefinition = indexComponent.autoIncrement(dsKey, modelDefinition, modelField, columnDefinition);
        return columnDefinition;
    }

    public String fieldConvertColumn(Map<String/*field*/, FieldWrapper> modelFieldMap, String fields,
                                     Set<String> logicColumns, boolean unique) {
        List<String> columns = new ArrayList<>();
        Set<String> appendUniques = null;
        if (unique) {
            appendUniques = new LinkedHashSet<>(logicColumns);
        }
        for (String field : fields.split(CharacterConstants.SEPARATOR_COMMA)) {
            field = field.trim();
            FieldWrapper modelField = modelFieldMap.get(field);
            if (null == modelField) {
                if (logicColumns.contains(field)) {
                    columns.add(field);
                    if (unique) {
                        appendUniques.remove(field);
                    }
                } else {
                    return null;
                }
            } else {
                columns.add(modelField.getColumn());
            }
        }
        if (unique && !CollectionUtils.isEmpty(appendUniques)) {
            columns.addAll(appendUniques);
        }
        return StringUtils.join(columns, CharacterConstants.SEPARATOR_COMMA);
    }

    public Set<String> fetchLogicColumns(String model) {
        return pamirsMapperConfiguration.getLogicColumnFetcher().fetchLogicColumns(model);
    }

    public Set<String> fetchDeprecatedColumns(String dsKey) {
        return pamirsMapperConfiguration.fetchPamirsDataConfiguration(dsKey).getDeprecatedColumns();
    }

    public String fetchLogicDeleteColumn(String model) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        if (pamirsTableInfo.getLogicDelete()) {
            return PamirsTableInfo.fetchPamirsTableInfo(model).getLogicDeleteColumn();
        }
        return null;
    }

    public boolean isLogicField(String model, String column) {
        Set<String> logicColumns = pamirsMapperConfiguration.getLogicColumnFetcher().fetchLogicColumns(model);
        return logicColumns.contains(column);
    }

}
