package pro.shushi.pamirs.ux.quickfilling.converter;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.ux.quickfilling.converter.defaults.StringConverter;
import pro.shushi.pamirs.ux.quickfilling.model.QuickFillingField;

/**
 * 快速填报上下文
 *
 * @author Adamancy Zhang at 14:08 on 2025-11-11
 */
@Slf4j
public class QuickFillingContext {

    private final String field;

    private final QuickFillingColumn column;

    private final QuickFillingConverter converter;

    public QuickFillingContext(ModelConfig modelConfig, ModelFieldConfig modelFieldConfig, QuickFillingField field) {
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(ttype)) {
            ttype = modelFieldConfig.getRelatedTtype();
        }
        this.field = field.getField();
        QuickFillingColumn column = new QuickFillingColumn(modelConfig.getModel(), this.field, ttype);
        column.setMulti(Boolean.TRUE.equals(modelFieldConfig.getMulti()));
        column.setRequired(Boolean.TRUE.equals(field.getRequired()));
        column.setValidate(!Boolean.FALSE.equals(field.getValidate()));
        column.setLabelFields(field.getLabelFields());
        if (TtypeEnum.ENUM.value().equals(ttype)) {
            column.setDictionary(modelFieldConfig.getDictionary());
        } else if (TtypeEnum.isRelationType(ttype)) {
            column.setReferences(modelFieldConfig.getReferences());
        }
        this.column = column;
        QuickFillingConverter converter = null;
        try {
            converter = QuickFillingConverterFactory.getApi(column);
        } catch (Throwable e) {
            log.warn("get quick filling converter api instance error.", e);
        }
        if (converter == null) {
            converter = new StringConverter(column);
        }
        this.converter = converter;
    }

    public QuickFillingColumn getColumn() {
        return column;
    }

    public String getField() {
        return field;
    }

    public QuickFillingConverter getConverter() {
        return converter;
    }
}
