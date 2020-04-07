package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enumclass.TtypeEnumCls;

@Base
@Model.model("base.Context")
@Model(displayName = "上下文", summary = "上下文")
public class Context extends TransientModel {

    @Base
    @Field(displayName = "键", required = true)
    private String key;

    @Base
    @Field(displayName = "值", required = true)
    private String value;

    @Base
    @Field(displayName = "值类型", required = true, defaultValue = "string")
    private TtypeEnumCls ttype;

}
