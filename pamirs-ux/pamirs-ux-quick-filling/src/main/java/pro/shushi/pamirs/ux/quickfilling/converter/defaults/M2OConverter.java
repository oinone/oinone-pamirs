package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.ux.quickfilling.converter.AbstractNonBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.util.*;

/**
 * 多对一类型转换
 *
 * @author Adamancy Zhang at 09:51 on 2025-11-28
 */
public class M2OConverter extends AbstractNonBasicQuickFillingConverter implements QuickFillingConverter {

    private final Map<String, List<QuickFillingRow>> matchValues;

    private final List<String> labelFields;

    private final List<String> labelFieldColumns;

    public M2OConverter(QuickFillingColumn column) {
        super(column);
        this.matchValues = new HashMap<>();
        String references = column.getReferences();
        List<String> labelFields = column.getLabelFields();
        if (CollectionUtils.isEmpty(labelFields)) {
            labelFields = PamirsSession.getContext().getSimpleModelConfig(references).getModelDefinition().getLabelFields();
        }
        if (CollectionUtils.isEmpty(labelFields)) {
            throw PamirsException.construct(QuickFillingExpEnumerate.LABEL_FIELDS_EMPTY_ERROR).errThrow();
        }
        List<String> validLabelFields = new ArrayList<>();
        List<String> labelFieldColumns = new ArrayList<>();
        for (String labelField : labelFields) {
            ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(references, labelField);
            if (modelFieldConfig == null) {
                continue;
            }
            String labelFieldColumn = modelFieldConfig.getColumn();
            if (StringUtils.isBlank(labelFieldColumn)) {
                continue;
            }
            labelFieldColumn = Configs.wrap(modelFieldConfig).getColumn();
            if (StringUtils.isNotBlank(labelFieldColumn)) {
                validLabelFields.add(modelFieldConfig.getLname());
                labelFieldColumns.add(labelFieldColumn);
            }
        }
        if (validLabelFields.isEmpty()) {
            throw PamirsException.construct(QuickFillingExpEnumerate.LABEL_FIELDS_EMPTY_ERROR).errThrow();
        }
        this.labelFields = validLabelFields;
        this.labelFieldColumns = labelFieldColumns;
    }

    @Override
    public void collect(QuickFillingRow row, String value) {
        if (isSkip(row, value)) {
            return;
        }
        matchValues.computeIfAbsent(value, k -> new ArrayList<>()).add(row);
    }

    @Override
    public void fill() {
        if (matchValues.isEmpty()) {
            return;
        }
        Set<String> values = matchValues.keySet();
        QuickFillingColumn column = getColumn();
        String references = column.getReferences();
        List<Object> list = DataShardingHelper.build().collectionSharding(values, (sublist) -> {
            QueryWrapper<Object> wrapper = Pops.query().from(references);
            // FIXME: zbh 20251128 此处不能使用 or 表达式，如果配置多个 labelFields 则需要进行多次查询
            wrapper.in(labelFieldColumns.get(0), sublist);
            wrapper.setSortable(false);
            wrapper.setBatchSize(-1);
            return Models.origin().queryListByWrapper(wrapper);
        });
        for (Object item : list) {
            for (String labelField : labelFields) {
                Object target = FieldUtils.getFieldValue(item, labelField);
                if (target == null) {
                    continue;
                }
                List<QuickFillingRow> matchedRows = matchValues.get(String.valueOf(target));
                if (matchedRows == null) {
                    continue;
                }
                for (QuickFillingRow matchedRow : matchedRows) {
                    setValue(matchedRow, item);
                }
                break;
            }
        }
    }
}
