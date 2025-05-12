package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.HookTypeEnum;

import java.util.List;

import static pro.shushi.pamirs.meta.domain.fun.Hook.MODEL_MODEL;

@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 14)
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(unique = {"executeNamespace,executeFun"})
@Model(displayName = "拦截器")
public class Hook extends MetaBaseModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "base.Hook";

    private static final long serialVersionUID = 3035858520330706953L;

    @Base
    @Field(displayName = "显示名称", required = true)
    private String displayName;

    @Base
    @Validation(check = checkModuleModule)
    @Field(displayName = "模块编码", required = true)
    private List<String> module;

    @Base
    @Validation(check = checkModelModel)
    @Field(displayName = "模型编码")
    private List<String> model;

    @Base
    @Field(displayName = "函数编码")
    private List<String> fun;

    @Base
    @Field.Enum
    @Field(displayName = "拦截函数类型", required = true)
    private List<FunctionTypeEnum> functionTypes;

    @Base
    @Field(displayName = "拦截器类型", required = true)
    private HookTypeEnum hookType;

    @Base
    @Field(displayName = "优先级", required = true, defaultValue = "999")
    private Integer priority;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"executeNamespace", "executeFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "执行函数", required = true)
    private FunctionDefinition executeFunction;

    @Base
    @Field(displayName = "执行函数命名空间", required = true)
    private String executeNamespace;

    @Base
    @Field(displayName = "执行函数编码", required = true)
    private String executeFun;

    @Field(displayName = "描述", required = true)
    private String description;

    @Base
    @Field(displayName = "是否激活")
    private Boolean active;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}
