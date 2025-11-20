package pro.shushi.pamirs.grouping.statistic;

import pro.shushi.pamirs.core.common.DateHelper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldFix;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 抽象统计API基类
 *
 * @author Adamancy Zhang at 15:15 on 2025-11-20
 */
public abstract class AbstractStatisticApi<T> implements StatisticApi<T> {

    private final String model;

    private final String field;

    private final String ttype;

    private final String invalidStatisticValue;

    public AbstractStatisticApi(StatisticField statisticField) {
        this.model = statisticField.getModel();
        this.field = statisticField.getField();
        this.ttype = statisticField.getTtype();
        this.invalidStatisticValue = statisticField.getInvalidStatisticValue();
    }

    public String getModel() {
        return model;
    }

    public String getField() {
        return field;
    }

    public String getTtype() {
        return ttype;
    }

    public String getInvalidStatisticValue() {
        return invalidStatisticValue;
    }

    @Override
    public void compute(List<T> data) {
        String field = getField();
        for (T item : data) {
            Object value = FieldUtils.getFieldValue(item, field);
            compute(item, value);
        }
    }

    protected abstract void compute(T data, Object value);

    protected String getNumberStatisticValue(BigDecimal number) {
        if (number == null) {
            return getInvalidStatisticValue();
        }
        String ttype = getTtype();
        if (TtypeEnum.FLOAT.value().equals(ttype) || TtypeEnum.MONEY.value().equals(ttype)) {
            return number.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return number.toPlainString();
    }

    protected String getDateStatisticValue(Date date) {
        if (date == null) {
            return getInvalidStatisticValue();
        }
        String format = FieldFix.fixFormat(PamirsSession.getContext().getModelField(getModel(), getField()).getModelField());
        return DateHelper.format(date, format);
    }

    protected String getPercentStatisticValue(long count, long total) {
        return StatisticHelper.computePercent(count, total);
    }
}
