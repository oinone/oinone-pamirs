package pro.shushi.pamirs.international.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.enmu.DirectionEnum;

@Model.model("resource.I18NStringConfig")
@Model(displayName = "字符串配置")
public class I18NStringConfig extends TransientModel {


    @Field.Enum
    @Field(displayName = "书写方向")
    private DirectionEnum direction;

    //ResourceCountry.phoneCode
    @Field.String
    @Field(displayName = "长途区号")
    private String phoneCode;

    //ResourceCountry.addrFormat
    @Field.String
    @Field(displayName = "地址显示格式")
    private String addrFormat;

    //ResourceCountry.namePosition
    @Field.Text
    @Field(displayName = "姓名显示规则", summary = "国家的姓名显示规则(如美国名在前，中国姓在前)")
    private String namePosition;
}
