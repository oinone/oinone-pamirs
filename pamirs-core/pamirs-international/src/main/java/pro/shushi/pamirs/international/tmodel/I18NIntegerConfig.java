package pro.shushi.pamirs.international.tmodel;


import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.enmu.DirectionEnum;

@Model.model("resource.I18NIntegerConfig")
@Model(displayName = "整数配置")
public class I18NIntegerConfig extends TransientModel {

    @Field.String
    @Field(displayName = "整数格式，千分位")
    private String thousandsSep;

    @Field.String
    @Field(displayName = "数字分组规则")
    private String groupingRule;

    @Field.Enum
    @Field(displayName = "书写方向")
    private DirectionEnum direction;
}
