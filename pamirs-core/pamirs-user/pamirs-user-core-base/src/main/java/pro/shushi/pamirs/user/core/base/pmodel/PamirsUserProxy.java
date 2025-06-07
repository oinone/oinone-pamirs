package pro.shushi.pamirs.user.core.base.pmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.user.api.model.PamirsUser;

@Model.model(PamirsUserProxy.MODEL_MODEL)
@Model(displayName = "用户导出代理")
public class PamirsUserProxy extends PamirsUser {

    public static final String MODEL_MODEL = "user.PamirsUserProxy";

    @Field(displayName = "角色编码")
    @Field.String
    private String roleCode;
}
