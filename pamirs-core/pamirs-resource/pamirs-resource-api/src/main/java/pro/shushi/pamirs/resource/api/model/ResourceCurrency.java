package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.resource.api.enmu.CurrencyPositionEnum;
import pro.shushi.pamirs.resource.api.enmu.CurrencyRoundingEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Model.MultiTableInherited(type = "RESOURCE_CURRENCY")
@Model.model(ResourceCurrency.MODEL_MODEL)
@Model.Advanced(name = "resourceCurrency", unique = {"code"})
@Model(displayName = "货币", labelFields = "name")
public class ResourceCurrency extends BaseResourceModel {

    public static final String MODEL_MODEL = "resource.ResourceCurrency";

    @Field.String
    @Field(required = true, displayName = "货币名称", translate = true)
    private String name;

    @Field.String
    @Field(required = true, displayName = "货币符号")
    private String symbol;

    @Field.Enum
    @Field(required = true, displayName = "符号位置")
    private CurrencyPositionEnum position;

    @Field.Enum
    @Field(required = true, displayName = "精确方式", summary = "取数值,精度四舍五入的方式", defaultValue = "ROUND_HALF_UP")
    private CurrencyRoundingEnum rounding;

    @Field.Integer
    @Field(required = true, displayName = "小数精度", defaultValue = "2")
    private Integer decimalPlaces;

    @Field.Boolean
    @Field(required = true, displayName = "是否激活", defaultValue = "true")
    private Boolean active;

    @Field.String
    @Field(required = true, displayName = "整数单位", summary = "货币的整数单位显示，如人民币显示元")
    private String currencyUnitLabel;

    @Field.String
    @Field(required = true, displayName = "小数单位", summary = "货币的小数单位显示，如人民币显示分")
    private String currencySubunitLabel;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceCurrency.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

    @Function
    public BigDecimal formatAmount(ResourceCurrency currency, BigDecimal amount) {
        if (null != currency && null != (currency.getRounding())) {
            switch (currency.getRounding()) {
                case ROUND_UP:
                    amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.UP);
                    break;
                case ROUND_DOWN:
                    amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.DOWN);
                    break;
                case ROUND_CEILING:
                    amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.CEILING);
                    break;
                case ROUND_FLOOR:
                    amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.FLOOR);
                    break;
                case ROUND_HALF_UP:
                    amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.HALF_UP);
                    break;
                case ROUND_HALF_DOWN:
                    amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.HALF_DOWN);
                    break;
                case ROUND_HALF_EVEN:
                    amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
                    break;
                default:
                    break;

            }
        }
        return amount;
    }

}
