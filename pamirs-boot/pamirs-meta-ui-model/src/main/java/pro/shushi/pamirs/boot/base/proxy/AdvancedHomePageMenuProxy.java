package pro.shushi.pamirs.boot.base.proxy;

import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author: xuxin
 * @createTime: 2024/06/11 15:07
 */
@Model(displayName = "系统配置菜单代理")
@Model.model(AdvancedHomePageMenuProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class AdvancedHomePageMenuProxy extends Menu {

    public static final String MODEL_MODEL = "base.AdvancedHomePageMenuProxy";

    /**
     * 支持使用树组件,且过滤条件默认指定了module
     */
    @Field.Relation(relationFields = {"parentName"}, referenceFields = {"name"})
    @Field(displayName = "上级菜单")
    private AdvancedHomePageMenuProxy parentProxy;
}
