package pro.shushi.pamirs.meta.domain.module;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 模块依赖
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.ModuleDependency")
@Model(displayName = "模块依赖", summary = "模块依赖", pk = {"module", "dependencyModule"}, labelFields = {"module", "dependencyModule"})
public class ModuleDependency extends BaseRelation {

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "module")
    @Field(displayName = "主模块", summary = "主模块")
    private ModuleDefinition moduleDefinition;

    @Base
    @Field(summary = "主模块", invisible = true)
    private String module;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "dependencyModule", referenceFields = "module")
    @Field(displayName = "依赖模块", summary = "依赖模块")
    private ModuleDefinition dependencyModuleDefinition;

    @Base
    @Field(summary = "依赖模块", invisible = true)
    private String dependencyModule;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述")
    private String remark;

}
