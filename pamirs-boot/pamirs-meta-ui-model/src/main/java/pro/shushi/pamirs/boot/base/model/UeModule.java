package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

/**
 * 前端模块定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
//@MetaModel(priority = 11)
@Base
@Model.Advanced(name = "module", unique = {"module"})
@Model.model(UeModule.MODEL_MODEL)
@Model(displayName = "视图模块", summary = "模块", labelFields = "displayName")
public class UeModule extends ModuleDefinition {

    private static final long serialVersionUID = -416854571119532579L;

    public static final String MODEL_MODEL = "base.UeModule";

    @Base
    @Field.Relation(relationFields = {"homePageModel", "homePageName"}, referenceFields = {"model", "name"})
    @Field.many2one
    @Field(displayName = "主页")
    private ViewAction homePage;

    @Base
    @Field.Relation(relationFields = "module")
    @Field.one2many
    @Field(displayName = "顶级菜单")
    private List<Menu> topMenus;

    @Base
    @Field.Relation(relationFields = "module")
    @Field.one2many(pageSize = 1000)
    @Field(displayName = "所有菜单")
    private List<Menu> allMenus;
}
