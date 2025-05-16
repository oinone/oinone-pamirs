package pro.shushi.pamirs.sys.setting.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author Wuxin
 * @Date 2024/6/26
 * @since 1.0
 */
@Base
@Model(displayName = "应用首页配置")
@Model.model(MultiTabsApplicationHomepageConfig.MODEL_MODEL)
public class MultiTabsApplicationHomepageConfig extends TransientModel {
    private static final long serialVersionUID = -6903376745399336410L;

    public static final String MODEL_MODEL = "sysSetting.MultiTabsApplicationHomepageConfig";


    @Field.Boolean
    @Field(displayName = "是否启用应用首页")
    private Boolean enabled;

    @Field.Boolean
    @Field(displayName = "首页是否自动隐藏")
    private Boolean autoInvisible;
}
