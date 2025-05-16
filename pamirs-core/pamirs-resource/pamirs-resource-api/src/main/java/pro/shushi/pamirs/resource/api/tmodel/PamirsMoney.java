package pro.shushi.pamirs.resource.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.math.BigDecimal;

@Model.model(PamirsMoney.MODEL_MODEL)
@Model(displayName = "钱")
public class PamirsMoney extends TransientModel {

    public static final String MODEL_MODEL = "base.PamirsMoney";

    @Field
    @Field.Money
    private BigDecimal value;

    @Field
    @Field.String
    private String currencyCode;

    public PamirsMoney(BigDecimal money) {
        setValue(money);
    }

    public PamirsMoney(BigDecimal money, String currencyCode) {
        setValue(money);
        setCurrencyCode(currencyCode);
    }
}
