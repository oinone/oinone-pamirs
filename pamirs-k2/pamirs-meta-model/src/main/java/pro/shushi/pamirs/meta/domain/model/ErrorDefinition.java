package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;

/**
 * 错误定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 8)
@Base
@Model.model(ErrorDefinition.MODEL_MODEL)
@Model.Advanced(unique = "clazz,name")
@Model(displayName = "错误定义", summary = "错误定义", labelFields = "displayName")
public class ErrorDefinition extends MetaBaseModel {

    private static final long serialVersionUID = -5066090659913119800L;

    public static final String MODEL_MODEL = "base.ErrorDefinition";

    @Base
    @Field.String
    @Field(displayName = "类", required = true)
    private String clazz;

    @Base
    @Field.String
    @Field(displayName = "名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "类型", required = true)
    private ErrorTypeEnum type;

    @Base
    @Field.String
    @Field(displayName = "错误码", required = true)
    private String code;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "描述", required = true, translate = true)
    private String msg;

    @Base
    @Field.Enum
    @Field(displayName = "状态", defaultValue = "true")
    private ActiveEnum state;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}
