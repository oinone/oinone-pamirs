package pro.shushi.pamirs.ux.quickfilling.converter;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础类型快速填报转换器
 *
 * @author Adamancy Zhang at 12:31 on 2025-11-27
 */
@Slf4j
public abstract class AbstractBasicQuickFillingConverter extends AbstractQuickFillingConverter implements QuickFillingConverter {

    public AbstractBasicQuickFillingConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    public void convert(QuickFillingRow row, String value) {
        if (isSkip(row, value)) {
            return;
        }
        QuickFillingColumn column = getColumn();
        String field = column.getField();
        if (column.isMulti()) {
            List<Object> results = new ArrayList<>();
            String[] valueList = value.split(CharacterConstants.SEPARATOR_COMMA);
            for (String valueItem : valueList) {
                if (StringUtils.isBlank(valueItem)) {
                    continue;
                }
                valueItem = valueItem.trim();
                Object target = convert0(row, valueItem);
                if (row.isFailure(field)) {
                    return;
                }
                if (target != null) {
                    results.add(target);
                }
            }
            if (isSkip(row, results)) {
                return;
            }
            setValue(row, results);
        } else {
            Object target = convert0(row, value);
            if (row.isFailure(field)) {
                return;
            }
            if (target != null) {
                setValue(row, target);
            }
        }
    }

    private Object convert0(QuickFillingRow row, String value) {
        try {
            return singleValueConvert(row, value);
        } catch (Exception e) {
            log.error("自动填报类型转换失败", e);
            validateError(row);
            return null;
        }
    }

    protected abstract Object singleValueConvert(QuickFillingRow row, String value);

    @Override
    public void collect(QuickFillingRow row, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill() {
        throw new UnsupportedOperationException();
    }
}
