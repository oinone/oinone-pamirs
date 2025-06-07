package pro.shushi.pamirs.framework.connectors.data.sql.test.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

@Base
@Model.Static
@Model.Persistence(underCamel = false, capitalMode = true)
@Model.model("test.TestUpperQueryModel")
@Model(displayName = "测试大写模型", summary = "测试大写模型")
public class TestUpperQueryModel extends BaseModel {

    @Base
    @Field(displayName = "键", required = true)
    private String key;

    @Base
    @Field(displayName = "值", required = true)
    private String value;

    @Base
    @Field(displayName = "值类型", required = true, defaultValue = "string")
    private TtypeEnum ttype;

    @Base
    @Field(displayName = "驼峰字段", required = true)
    private String camelField;

    @Base
    @Field.Advanced(onlyColumn = false)
    @Field(displayName = "别名字段", required = true)
    private String asPropertyField;

}
