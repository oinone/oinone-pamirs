package pro.shushi.pamirs.my.pmodel;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

@Base
@Model(displayName = "个人中心用户代理")
@Model.model(MyPamirsUserProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class MyPamirsUserProxy extends PamirsUser {
    public static final String MODEL_MODEL = "my.MyPamirsUserProxy";

}
