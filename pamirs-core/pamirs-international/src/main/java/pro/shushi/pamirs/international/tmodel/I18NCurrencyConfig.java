package pro.shushi.pamirs.international.tmodel;


import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.enmu.CurrencyPositionEnum;
import pro.shushi.pamirs.resource.api.enmu.DirectionEnum;

@Model.model("resource.I18NCurrencyConfig")
@Model(displayName = "货币配置")
public class I18NCurrencyConfig extends TransientModel {

    @Field.String
    @Field(displayName = "货币符号")
    private String symbol;

    @Field.Enum
    @Field(displayName = "货币符号位置")
    private CurrencyPositionEnum position;

    @Field.Integer
    @Field(displayName = "小数精度")
    private Integer decimalPlaces;

    @Field.String
    @Field(displayName = "小数点格式")
    private String decimalPoint;

    @Field.String
    @Field(displayName = "整数单位")
    private String currencyUnitLabel;

    @Field.String
    @Field(displayName = "小数单位")
    private String currencySubunitLabel;

    @Field.Enum
    @Field(displayName = "书写方向")
    private DirectionEnum direction;

    @Field.String
    @Field(displayName = "数字分组规则")
    private String groupingRule;

    @Field.String
    @Field(displayName = "整数格式")
    private String thousandsSep;
}
