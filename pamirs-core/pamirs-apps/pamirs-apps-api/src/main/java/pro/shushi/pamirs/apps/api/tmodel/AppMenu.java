package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * AppMenu
 *
 * @author yakir on 2022/10/09 14:06.
 */
@Base
@Model(displayName = "产品首页设置")
@Model.model(AppMenu.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class AppMenu extends Menu {

    private static final long serialVersionUID = 1366991472776880499L;

    public static final String MODEL_MODEL = "apps.AppMenu";

}
