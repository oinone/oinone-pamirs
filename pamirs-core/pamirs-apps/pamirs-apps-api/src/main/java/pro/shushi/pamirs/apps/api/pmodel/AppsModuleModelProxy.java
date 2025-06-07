package pro.shushi.pamirs.apps.api.pmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

@Base
@Model(displayName = "Apps模块模型代理")
@Model.model(AppsModuleModelProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class AppsModuleModelProxy extends ModelDefinition {

    public static final String MODEL_MODEL = "apps.AppsModuleModelProxy";

    @Field.String
    @Field(displayName = "根模块编码,用于筛选模块及其依赖模块下的模型")
    private String rootModuleModule;
}
