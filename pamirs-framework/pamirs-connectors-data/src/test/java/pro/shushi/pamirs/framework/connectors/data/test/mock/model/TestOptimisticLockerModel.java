package pro.shushi.pamirs.framework.connectors.data.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.VersionModel;

@Base
@Model.Static
@Model.Advanced(table = "test")
@Model.model(TestOptimisticLockerModel.modelModel)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestOptimisticLockerModel extends VersionModel {

    public static final String modelModel = "test.TestOptimisticLockerModel";

    private static final long serialVersionUID = 8038025777333269458L;

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

}
