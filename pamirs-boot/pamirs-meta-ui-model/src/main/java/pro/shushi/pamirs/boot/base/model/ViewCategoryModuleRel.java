package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 模块视图分组
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 22, core = Class.class)
@Base
@Model(displayName = "视图分组应用模块关系")
@Model.model(ViewCategoryModuleRel.MODEL_MODEL)
@Model.Advanced(relationship = NullableBoolEnum.TRUE, unique = {"categoryId,moduleId"})
public class ViewCategoryModuleRel extends MetaBaseModel {

    private static final long serialVersionUID = 2979594215199154354L;

    public final static String MODEL_MODEL = "base.ViewCategoryModuleRel";

    @Field(displayName = "分组ID", index = true, required = true)
    private Long categoryId;

    @Field(displayName = "模块ID", index = true, required = true)
    private Long moduleId;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}
