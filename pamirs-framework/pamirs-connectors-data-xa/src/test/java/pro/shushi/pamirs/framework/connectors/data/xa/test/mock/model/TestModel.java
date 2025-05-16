package pro.shushi.pamirs.framework.connectors.data.xa.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

@Base
@Model.Static
@Model.Advanced(table = "test")
@Model.Persistence(logicDelete = false)
@Model.model(TestModel.modelModel)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestModel extends IdModel {

    public static final String modelModel = "test.TestModel";

    private static final long serialVersionUID = -1409318863257398804L;

    @Base
    @Field.String
    @Field(displayName = "模块", unique = true, required = true)
    private String module;

    @Base
    @Field.String
    @Field(displayName = "版本", required = true)
    private String version;

    @Base
    @Field.Text
    @Field(displayName = "关系", required = true)
    private String relation;

    @Base
    @Field.Integer
    @Field.field("opt")
    @Field(displayName = "乐观锁", defaultValue = "0", required = true)
    private Long optVersion;

}
