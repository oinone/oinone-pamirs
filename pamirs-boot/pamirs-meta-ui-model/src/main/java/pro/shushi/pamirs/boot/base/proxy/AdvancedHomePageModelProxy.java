package pro.shushi.pamirs.boot.base.proxy;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author ranjingnian
 */
@Base
@Model(displayName = "首页模型代理")
@Model.model(AdvancedHomePageModelProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class AdvancedHomePageModelProxy extends ModelDefinition {

    public static final String MODEL_MODEL = "base.AdvancedHomePageModelProxy";

    @Field.String
    @Field(displayName = "根模块编码,用于筛选模块及其依赖模块下的模型")
    private String rootModuleModule;
}
