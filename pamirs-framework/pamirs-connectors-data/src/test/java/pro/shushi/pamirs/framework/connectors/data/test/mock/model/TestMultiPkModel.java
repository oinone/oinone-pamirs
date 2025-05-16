package pro.shushi.pamirs.framework.connectors.data.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

@Base
@Model.Static
@Model.Advanced(table = "test_multi_pk")
@Model.model(TestMultiPkModel.modelModel)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestMultiPkModel extends IdModel {

    public static final String modelModel = "test.TestMultiPkModel";

    private static final long serialVersionUID = 5878274282050434398L;

    @Base
    @Field.PrimaryKey(1)
    @Field.String
    @Field(displayName = "模块", required = true)
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
