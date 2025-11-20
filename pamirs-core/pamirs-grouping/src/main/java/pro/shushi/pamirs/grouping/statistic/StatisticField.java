package pro.shushi.pamirs.grouping.statistic;

/**
 * 统计字段
 *
 * @author Adamancy Zhang at 15:12 on 2025-11-20
 */
public class StatisticField {

    private final String model;

    private final String field;

    private final String ttype;

    private final String invalidStatisticValue;

    public StatisticField(String model, String field, String ttype, String invalidStatisticValue) {
        this.model = model;
        this.field = field;
        this.ttype = ttype;
        this.invalidStatisticValue = invalidStatisticValue;
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
}
