package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.boot.base.enmu.AppConfigModeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author wangxian
 * date 2023/12/13
 */
@Base
@Model(displayName = "侧边栏样式")
@Model.model(SideBarTheme.MODEL_MODEL)
public class SideBarTheme extends TransientModel {

    public static final String MODEL_MODEL = "base.config.SideBarTheme";

    @Base
    @Field.Enum
    @Field(displayName = "主题")
    private AppConfigModeEnum mode;

    @Base
    @Field(displayName = "样式")
    private String theme;

}
