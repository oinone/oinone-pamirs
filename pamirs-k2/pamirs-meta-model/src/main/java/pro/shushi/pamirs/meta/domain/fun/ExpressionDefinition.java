package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.ExpressionContextConstants;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionScopeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 表达式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 40)
@Base
@Model.Advanced(unique = {"model,type,location,expression"}, priority = 26)
@Model.model(ExpressionDefinition.MODEL_MODEL)
@Model(displayName = "表达式", summary = "表达式")
public class ExpressionDefinition extends MetaBaseModel implements MetaCheckConstants {

    private static final long serialVersionUID = -7170240546930698804L;

    public static final String MODEL_MODEL = "base.ExpressionDefinition";

    @Base
    @Field(displayName = "作用域", summary = "作用域值为模型编码")
    private String model;

    @Base
    @Field(displayName = "作用位置")
    private String location;

    @Base
    @Validation(check = checkExpression)
    @Field(displayName = "表达式", summary = "表达式", required = true)
    private String expression;

    @Base
    @Field(displayName = "执行域", summary = "执行域")
    private FunctionScopeEnum scope;

    @Base
    @Field(displayName = "类型", summary = "表达式的类型", required = true)
    private ComputeSceneEnum type;

    @Base
    @Field(displayName = "所属模块", summary = "所属模块")
    private String module;

    @Base
    @Field(displayName = "备注", summary = "备注")
    private String remark;

    @Base
    @Field(displayName = "提示")
    private String tips;

    @Base
    @Field(displayName = "错误提示")
    private String error;

    @Base
    @Field(displayName = "错误级别", summary = "错误级别", defaultValue = "error")
    private InformationLevelEnum level;

    @Base
    @Field(displayName = "错误类型", summary = "如果错误级别为错误，则可指定错误类型", defaultValue = "BIZ_ERROR")
    private ErrorTypeEnum errorType;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = "100")
    private Integer priority;

    @Override
    public String getSignModel() {
        return ExpressionDefinition.MODEL_MODEL;
    }

    public static String sign(ComputeSceneEnum type, String model, String location, String expression) {
        return type.value() + CharacterConstants.SEPARATOR_OCTOTHORPE + model
                + CharacterConstants.SEPARATOR_OCTOTHORPE + location
                + CharacterConstants.SEPARATOR_OCTOTHORPE + expression;
    }

    public static String key(ComputeSceneEnum computeScene, String model, String sign) {
        return key(computeScene.value(), model, sign);
    }

    public static String key(String computeScene, String model, String sign) {
        return computeScene +
                CharacterConstants.SEPARATOR_OCTOTHORPE + model +
                CharacterConstants.SEPARATOR_OCTOTHORPE + sign;
    }

    public static Map<String, Object> constructContext() {
        return constructContext(null, null);
    }

    public static Map<String, Object> constructContext(String model) {
        return constructContext(model, null);
    }

    public static Map<String, Object> constructContext(String model, String field) {
        Map<String, Object> map = new HashMap<>();
        if (null != model) {
            map.put(ExpressionContextConstants.ACTIVE_MODEL, model);
        }
        if (null != field) {
            map.put(ExpressionContextConstants.ACTIVE_FIELD, field);
        }
        return map;
    }

}
