package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;

import java.util.List;

/**
 * 错误组
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 8)
@Base
@Model.model(ErrorsDefinition.MODEL_MODEL)
@Model.Advanced(unique = "clazz", priority = 14)
@Model(displayName = "错误组", summary = "错误组", labelFields = "displayName")
public class ErrorsDefinition extends MetaBaseModel {

    private static final long serialVersionUID = 5347049295456848588L;

    public static final String MODEL_MODEL = "base.ErrorsDefinition";

    @Base
    @Field.String
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "描述", translate = true)
    private String summary;

    @Base
    @Field.String
    @Field(displayName = "类", required = true)
    private String clazz;

    @Base
    @Field.String
    @Field(displayName = "模块", required = true)
    private String module;

    @Base
    @Field.Relation(relationFields = "clazz")
    @Field(displayName = "错误列表", required = true)
    private List<ErrorDefinition> errorDefinitions;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}
