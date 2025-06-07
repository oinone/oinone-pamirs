package pro.shushi.pamirs.meta.domain.module;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModuleMiddlewareTypeEnum;

/**
 * 应用所需的中间件类型
 *
 * @author shier
 */
@Base
@Model.model(ModuleMiddleWare.MODEL_MODEL)
@Model(displayName = "应用中间件", summary = "应用中间件")
public class ModuleMiddleWare extends TransientModel {

    public final static String MODEL_MODEL = "base.ModuleMiddleWare";

    @Base
    @Field.Enum
    @Field(displayName = "类型")
    private ModuleMiddlewareTypeEnum type;

}
