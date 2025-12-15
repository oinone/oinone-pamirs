package pro.shushi.pamirs.ux.grouping.statistic;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldFix;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
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
        if (TtypeEnum.FLOAT.value().equals(ttype) || TtypeEnum.MONEY.value().equals(ttype)) {
            Integer decimal = PamirsSession.getContext().getModelField(getModel(), getField()).getModelField().getDecimal();
            if (decimal == null) {
                decimal = 2;
            }
            return number.setScale(decimal, RoundingMode.HALF_UP).toPlainString();
        }
        return number.toPlainString();
    }

    protected String getDateStatisticValue(Date date) {
        if (date == null) {
            return getInvalidStatisticValue();
        }
        String format = FieldFix.fixFormat(PamirsSession.getContext().getModelField(getModel(), getField()).getModelField());
        assert format != null;
        return new SimpleDateFormat(format).format(date);
    }

    protected String getPercentStatisticValue(long count, long total) {
        return StatisticHelper.computePercent(count, total);
    }
}
