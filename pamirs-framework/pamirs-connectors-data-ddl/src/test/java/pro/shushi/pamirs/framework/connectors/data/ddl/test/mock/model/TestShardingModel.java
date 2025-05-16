package pro.shushi.pamirs.framework.connectors.data.ddl.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

@Base
@Model.Static
@Model.model(TestShardingModel.modelModel)
@Model.Ds("test")
@Model(displayName = "测试模型", summary = "测试模型")
public class TestShardingModel extends IdModel {

    public static final String modelModel = "test.TestShardingModel";

    private static final long serialVersionUID = -1409318863257398804L;

    @Field(displayName = "模块", unique = true, required = true)
    private String module;

}
