package pro.shushi.pamirs.boot.base.model;

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
 * 视图分组
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 22, core = Class.class)
@Base
@Model(displayName = "视图分组")
@Model.model(ViewCategory.MODEL_MODEL)
@Model.Advanced(unique = {"name"})
public class ViewCategory extends MetaBaseModel {

    private static final long serialVersionUID = 149351653037033780L;

    public final static String MODEL_MODEL = "base.ViewCategory";

    @Base
    @Field.String(size = 64)
    @Field(displayName = "分组名称", required = true)
    private String name;

    @Base
    @Field(displayName = "可见性", summary = "显隐", defaultValue = "true")
    private ActiveEnum active;

    @Base
    @Field(displayName = "视图分组模块关系")
    @Field.many2many(through = ViewCategoryModuleRel.MODEL_MODEL, relationFields = {"categoryId"}, referenceFields = {"moduleId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    private List<ModuleDefinition> moduleList;

    @Base
    @Field(displayName = "视图分组视图关系")
    @Field.one2many
    @Field.Relation(relationFields = {"id"}, referenceFields = {"categoryId"})
    private List<View> viewList;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = "0")
    private Long priority;

    @Override
    public String getSignModel() {
        return ViewCategory.MODEL_MODEL;
    }

}
