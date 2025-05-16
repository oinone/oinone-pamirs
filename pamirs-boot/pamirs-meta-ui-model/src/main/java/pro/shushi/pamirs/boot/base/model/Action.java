package pro.shushi.pamirs.boot.base.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.boot.base.model.Action.MODEL_MODEL;
import static pro.shushi.pamirs.meta.annotation.Field.serialize.JSON;

@MetaSimulator(onlyBasicTypeField = false)
//@MetaModel(priority = 8)
@Base
@Model.MultiTable(typeField = "actionType")
@Model.Advanced(name = "action", unique = "model, name", priority = 19)
@Model.model(MODEL_MODEL)
@Model(displayName = "动作", summary = "动作", labelFields = "displayName")
public class Action extends MetaBaseModel implements MetaCheckConstants {

    private static final long serialVersionUID = 5683713525673879486L;

    public static final String MODEL_MODEL = "base.Action";

    @Base
    @Field.String
    @Field(displayName = "显示名称", translate = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "显示文字", translate = true)
    private String label;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model")
    @Field(required = true, displayName = "模型")
    private ModelDefinition modelDefinition;

    @Base
    @Field(required = true)
    private String model;

    @Base
    @Field.Related(related = {"modelDefinition", "name"})
    @Field(invisible = true)
    private String modelName;

    @Base
    @Field.String
    @Field(required = true, displayName = "api名称", index = true)
    private String name;

    @Base
    @Field.Enum
    @Field(displayName = "类型", required = true)
    private ActionTypeEnum actionType;

    @Base
    @Field.Enum
    @Field(multi = true, displayName = "绑定类型", summary = "action绑定在源模型上的哪些视图上")
    private List<ViewTypeEnum> bindingType;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "bindingViewName"}, referenceFields = {"model", "name"})
    @Field(displayName = "绑定视图", summary = "action绑定在源模型上的哪个自定义view上")
    private View bindingView;

    @Base
    @Field(invisible = true)
    private String bindingViewName;

    @Base
    @Field.Enum
    @Field(displayName = "上下文类型")
    private ActionContextTypeEnum contextType;

    @Base
    @Field.String
    @Field(displayName = "简介", summary = "描述摘要", translate = true)
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述", summary = "描述详情", translate = true)
    private String description;

    @Base
    @Field.Text
    @Field(displayName = "禁用规则")
    private String disable;

    @Base
    @Field.Text
    @Field(displayName = "显隐", summary = "客户端显隐的表达式")
    private String invisible;

    @Base
    @Field.Text
    @Field(displayName = "过滤动作", summary = "服务端过滤动作的表达式")
    private String rule;

    @Base
    @Field.String(size = 1024)
    @Field(displayName = "数据映射", serialize = JSON, summary = "数据映射")
    private Map<String, Object> mapping;

    @Base
    @Field.String(size = 1024)
    @Field(displayName = "上下文", serialize = JSON, summary = "上下文")
    private Map<String, Object> context;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = "100")
    private Integer priority;

    @Field.String
    @Field(displayName = "会话路径", store = NullableBoolEnum.FALSE)
    private String sessionPath;

    public Action setFunctionDefinition(FunctionDefinition functionDefinition) {
        return this;
    }

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    public Action construct(Action data) {
        if (StringUtils.isBlank(data.getName())) {
            data.setName(Action.class.getSimpleName()
                    + CharacterConstants.SEPARATOR_UNDERLINE
                    + actionType
                    + CharacterConstants.SEPARATOR_UNDERLINE
                    + UUIDUtil.getUUIDNumberString());
        }
        if (StringUtils.isBlank(data.getDisplayName())) {
            data.setDisplayName(data.getName());
        }
        return data;
    }

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "modelDefinition", "bindingView");
    }

    public static String sign(String model, String name) {
        return model + CharacterConstants.SEPARATOR_DOT + name;
    }

}
