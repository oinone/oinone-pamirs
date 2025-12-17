package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.ux.quickfilling.converter.AbstractNonBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.util.ArrayList;
import java.util.List;

/**
 * 关联关系类型转换
 *
 * @author Adamancy Zhang at 21:43 on 2025-12-10
 */
public abstract class AbstractRelationConverter extends AbstractNonBasicQuickFillingConverter implements QuickFillingConverter {

    protected final List<String> labelFields;

    protected final List<String> labelFieldColumns;

    public AbstractRelationConverter(QuickFillingColumn column) {
        super(column);
        String references = column.getReferences();
        List<String> labelFields = column.getLabelFields();
        if (CollectionUtils.isEmpty(labelFields)) {
            labelFields = PamirsSession.getContext().getSimpleModelConfig(references).getModelDefinition().getLabelFields();
        }
        if (CollectionUtils.isEmpty(labelFields)) {
            throw PamirsException.construct(QuickFillingExpEnumerate.LABEL_FIELDS_EMPTY_ERROR, column.getModel(), column.getField(), column.getTtype()).errThrow();
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
            throw PamirsException.construct(QuickFillingExpEnumerate.LABEL_FIELDS_EMPTY_ERROR, column.getModel(), column.getField(), column.getTtype()).errThrow();
        }
        this.labelFields = validLabelFields;
        this.labelFieldColumns = labelFieldColumns;
    }
}
