package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.enmu.FunctionScopeEnum;

/**
 * 计算函数配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 41)
@Base
@Model.Advanced(index = {"model,type,location"}, priority = 27)
@Model.model(ComputeDefinition.MODEL_MODEL)
@Model(displayName = "计算函数配置", summary = "计算函数配置")
public class ComputeDefinition extends MetaBaseModel implements MetaCheckConstants {

    private static final long serialVersionUID = -1279650913732302182L;

    public static final String MODEL_MODEL = "base.ComputeDefinition";

    @Base
    @Field(displayName = "作用域", summary = "作用域值为模型编码")
    private String model;

    @Base
    @Field(displayName = "作用位置")
    private String location;

    @Base
    @Field(displayName = "函数编码", summary = "函数编码", required = true)
    private String fun;

    @Base
    @Field(displayName = "执行域", summary = "执行域")
    private FunctionScopeEnum scope;

    @Base
    @Field(displayName = "类型", summary = "函数类型", required = true)
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
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = "100")
    private Integer priority;

    @Override
    public String getSignModel() {
        return ComputeDefinition.MODEL_MODEL;
    }

    public static String sign(ComputeSceneEnum type, String model, String location, String fun) {
        return type.value() + CharacterConstants.SEPARATOR_OCTOTHORPE + model
                + CharacterConstants.SEPARATOR_OCTOTHORPE + location
                + CharacterConstants.SEPARATOR_OCTOTHORPE + fun;
    }

}
