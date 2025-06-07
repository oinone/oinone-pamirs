package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;

import java.util.List;

/**
 * ModelCategory
 *
 * @author yakir on 2021/08/04 21:35.
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 4, core = Class.class)
@Base
@Model(displayName = "模型分组")
@Model.model(ModelCategory.MODEL_MODEL)
@Model.Advanced(unique = {"name"})
public class ModelCategory extends MetaBaseModel {

    private static final long serialVersionUID = 1713308243319330872L;

    public final static String MODEL_MODEL = "base.ModelCategory";

    @Base
    @Field(displayName = "分组名称", required = true)
    private String name;

    @Base
    @Field(displayName = "可见性", summary = "显隐", defaultValue = "true")
    private ActiveEnum active;

    @Base
    @Field(displayName = "模型分组模块关系")
    @Field.many2many(through = ModelCategoryModuleRel.MODEL_MODEL, relationFields = {"categoryId"}, referenceFields = {"moduleId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    private List<ModuleDefinition> moduleDefine;

    @Base
    @Field(displayName = "模型分组模型关系")
    @Field.one2many
    @Field.Relation(relationFields = {"id"}, referenceFields = {"categoryId"})
    private List<ModelDefinition> modelDefine;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = "0")
    private Long priority;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }
}
