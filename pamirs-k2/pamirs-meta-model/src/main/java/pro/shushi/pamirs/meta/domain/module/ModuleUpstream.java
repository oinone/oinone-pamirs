package pro.shushi.pamirs.meta.domain.module;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 上游模块
 * <p>
 * 用于定义标品模块和扩展模块间关系
 * </p>
 *
 * @author Adamancy Zhang at 14:18 on 2024-03-13
 */
@MetaSimulator(onlyBasicTypeField = false)
@Base
@Model.model("base.ModuleUpstream")
@Model(displayName = "上游模块", summary = "上游模块", labelFields = {"module", "upstreamModule"})
public class ModuleUpstream extends BaseRelation {

    private static final long serialVersionUID = 3435296012958155570L;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "module")
    @Field(displayName = "主模块", summary = "主模块")
    private ModuleDefinition moduleDefinition;

    @Base
    @Field.PrimaryKey
    @Field(summary = "主模块编码", invisible = true)
    private String module;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "upstreamModule", referenceFields = "module")
    @Field(displayName = "上游模块", summary = "上游模块")
    private ModuleDefinition upstreamModuleDefinition;

    @Base
    @Field.PrimaryKey(1)
    @Field(summary = "上游模块编码", invisible = true)
    private String upstreamModule;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述")
    private String remark;

}


