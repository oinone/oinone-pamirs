package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * ModelCategoryModuleRel
 *
 * @author yakir on 2021/08/05 21:51.
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 4, core = Class.class)
@Base
@Model(displayName = "模型分组应用模块关系")
@Model.model(ModelCategoryModuleRel.MODEL_MODEL)
@Model.Advanced(relationship = NullableBoolEnum.TRUE, unique = {"categoryId,moduleId"})
public class ModelCategoryModuleRel extends MetaBaseModel {

    private static final long serialVersionUID = -1492514225546723261L;

    public final static String MODEL_MODEL = "base.ModelCategoryModuleRel";

    @Field(displayName = "模型分组ID", required = true)
    private Long categoryId;

    @Field(displayName = "模块ID", index = true, required = true)
    private Long moduleId;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }
}
