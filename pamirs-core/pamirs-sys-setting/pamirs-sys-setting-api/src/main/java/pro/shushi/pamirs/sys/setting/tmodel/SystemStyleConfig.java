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
@Model(displayName = "系统风格配置")
@Model.model(SystemStyleConfig.MODEL_MODE)
public class SystemStyleConfig extends TransientModel {
    private static final long serialVersionUID = 1929480739118048558L;

    public static final String MODEL_MODE = "sysSetting.SystemStyleConfig";

    @Field(displayName = "多tab栏样式")
    private MultiTabConfig multiTabConfig;

    @Field(displayName = "侧边栏样式")
    private SideBarConfig sideBarConfig;

}
