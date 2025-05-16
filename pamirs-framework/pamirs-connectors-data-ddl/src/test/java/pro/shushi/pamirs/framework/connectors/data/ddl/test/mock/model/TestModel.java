package pro.shushi.pamirs.framework.connectors.data.ddl.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.VersionModel;

import java.util.Date;

@Base
@Model.Static
@Model.model(TestModel.modelModel)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestModel extends VersionModel {

    public static final String modelModel = "test.TestModel";

    private static final long serialVersionUID = -1409318863257398804L;

    @Field(displayName = "模块", unique = true, required = true)
    private String module;

    @Field(displayName = "版本", required = true)
    private Integer version;

    @Field(displayName = "关系", required = true)
    private Boolean relation;

    @Field(displayName = "时间")
    private Date date;

}
