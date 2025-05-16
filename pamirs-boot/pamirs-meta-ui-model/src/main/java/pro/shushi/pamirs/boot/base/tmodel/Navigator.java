package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

@Base
@Model.model(Navigator.MODEL_MODEL)
@Model(displayName = "导航", summary = "导航", labelFields = {"displayName"})
public class Navigator extends TransientModel {

    private static final long serialVersionUID = 4122092752188305855L;

    public static final String MODEL_MODEL = "base.Navigator";

    @Base
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field(displayName = "api名称", required = true)
    private String name;

}
