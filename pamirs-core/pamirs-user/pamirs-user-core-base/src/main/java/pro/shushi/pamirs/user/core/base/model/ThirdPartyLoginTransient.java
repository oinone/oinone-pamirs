package pro.shushi.pamirs.user.core.base.model;

import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model(displayName = "第三方登录临时模型")
@Model.model("user.ThirdPartyLoginTransient")
public class ThirdPartyLoginTransient extends TransientModel {

    @Field.many2one
    @Field(displayName = "跳转url")
    private UrlAction url;
}
