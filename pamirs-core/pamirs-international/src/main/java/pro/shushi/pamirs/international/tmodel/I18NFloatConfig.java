package pro.shushi.pamirs.international.tmodel;


import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.enmu.DirectionEnum;

@Model.model("resource.I18NFloatConfig")
@Model(displayName = "浮点数配置")
public class I18NFloatConfig extends TransientModel {

    @Field.String
    @Field(displayName = "小数点格式")
    private String decimalPoint;

    @Field.String
    @Field(displayName = "整数格式，千分位")
    private String thousandsSep;

    @Field.String
    @Field(displayName = "数字分组规则")
    private String groupingRule;

    @Field.Enum
    @Field(displayName = "书写方向")
    private DirectionEnum direction;

    @Field.Integer
    @Field(displayName = "小数精度")
    private Integer decimalPlaces;
}
