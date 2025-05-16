package pro.shushi.pamirs.framework.test.data.dependency1.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

@Base
@Model.model(TestShardingModel.MODEL_MODEL)
@Model.Ds("ds")
@Model(displayName = "测试模型", summary = "测试模型")
public class TestShardingModel extends IdModel {

    public static final String MODEL_MODEL = "test.TestShardingModel";
    private static final long serialVersionUID = 8301964791642379606L;

    @Field(displayName = "模块", unique = true, required = true)
    private String module;
    
    @Field
    private String summary;

}
