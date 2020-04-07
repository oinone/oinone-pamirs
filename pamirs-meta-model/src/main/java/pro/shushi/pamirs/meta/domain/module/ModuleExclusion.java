package pro.shushi.pamirs.meta.domain.module;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 模块互斥
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.ModuleExclusion")
@Model(displayName = "模块互斥", summary = "模块互斥", pk = {"module", "excludeModule"}, labelFields = {"module", "excludeModule"})
public class ModuleExclusion extends BaseRelation {

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "module")
    @Field(displayName = "主模块", summary = "主模块")
    private ModuleDefinition moduleDefinition;

    @Base
    @Field(summary = "主模块编码", invisible = true)
    private String module;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "excludeModule", referenceFields = "module")
    @Field(displayName = "互斥模块", summary = "互斥模块")
    private ModuleDefinition excludeModuleDefinition;

    @Base
    @Field(summary = "互斥模块编码", invisible = true)
    private String excludeModule;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述")
    private String remark;

}


