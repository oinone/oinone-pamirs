package pro.shushi.pamirs.ux.quickfilling.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Collection;

/**
 * 抽象快速填报转换器
 *
 * @author Adamancy Zhang at 12:31 on 2025-11-27
 */
@Slf4j
public abstract class AbstractQuickFillingConverter implements QuickFillingConverter {

    private final QuickFillingColumn column;

    private final String field;

    public AbstractQuickFillingConverter(QuickFillingColumn column) {
        this.column = column;
        this.field = column.getField();
    }

    public QuickFillingColumn getColumn() {
        return column;
    }

    protected boolean isSkip(QuickFillingRow row, String value) {
        if (StringUtils.isBlank(value)) {
            if (column.isRequired()) {
                row.validateRequired(field);
            }
            return true;
        }
        return false;
    }

    protected boolean isSkip(QuickFillingRow row, Collection<?> values) {
        if (CollectionUtils.isEmpty(values)) {
            if (column.isRequired()) {
                row.validateRequired(field);
            }
            return true;
        }
        return false;
    }

    protected Object getValue(QuickFillingRow row) {
        return row.getValue(field);
    }

    protected void setValue(QuickFillingRow row, Object value) {
        row.setValue(field, value);
    }

    protected void validateError(QuickFillingRow row) {
        if (this.column.isValidate()) {
            row.validateError(field);
        }
    }

    protected void validateError(QuickFillingRow row, String message) {
        if (this.column.isValidate()) {
            row.validateError(field, message);
        }
    }
}
