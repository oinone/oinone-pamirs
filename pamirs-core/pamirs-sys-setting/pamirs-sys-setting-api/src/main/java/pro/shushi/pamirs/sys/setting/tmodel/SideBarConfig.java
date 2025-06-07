package pro.shushi.pamirs.sys.setting.tmodel;

import pro.shushi.pamirs.boot.base.enmu.AppConfigModeEnum;
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
@Model(displayName = "侧边栏样式")
@Model.model(SideBarConfig.MODEL_MODEL)
public class SideBarConfig extends TransientModel {
    private static final long serialVersionUID = -2660338080487183261L;

    public static final String MODEL_MODEL = "sysSetting.SideBarConfig";

    @Base
    @Field.Enum
    @Field(displayName = "主题")
    private AppConfigModeEnum mode;

    @Base
    @Field(displayName = "样式")
    private String theme;
}
