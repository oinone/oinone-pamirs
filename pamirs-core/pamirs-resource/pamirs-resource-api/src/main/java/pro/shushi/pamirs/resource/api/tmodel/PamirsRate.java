package pro.shushi.pamirs.resource.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import java.math.BigDecimal;

@Model.model(PamirsRate.MODEL_MODEL)
@Model(displayName = "百分比", summary = "范围0-100%")
public class PamirsRate extends TransientModel {

    public static final String MODEL_MODEL = "base.PamirsRate";

    @Field
    @Field.Float
    private BigDecimal value;

    public PamirsRate(BigDecimal rate) {
        if (rate != null) {
            if (rate.compareTo(BigDecimal.ZERO) < 0) {
                throw PamirsException.construct(ExpEnumerate.RATE_LT_ZERO).errThrow();
            }
            if (rate.compareTo(BigDecimal.ONE) > 0) {
                throw PamirsException.construct(ExpEnumerate.RATE_GT_100).errThrow();
            }
        }
        setValue(rate);
    }
}
