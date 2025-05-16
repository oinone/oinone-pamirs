package pro.shushi.pamirs.dev.tools.init;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.dev.tools.model.DictionaryOverview;
import pro.shushi.pamirs.dev.tools.model.ExtPointOverview;
import pro.shushi.pamirs.dev.tools.model.FunctionOverview;
import pro.shushi.pamirs.dev.tools.model.ModelOverview;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

@UxMenus public class DevToolsMenus implements ViewActionConstants {



    @UxMenu("缓存对比DB") class DevToolsMenu{
        @UxMenu("模型对比查询")@UxRoute(model= ModelOverview.MODEL_MODEL,viewType= ViewTypeEnum.FORM,viewName = "") class ModelOverviewMenu{ }
        @UxMenu("函数对比查询")@UxRoute(model= FunctionOverview.MODEL_MODEL,viewType= ViewTypeEnum.FORM,viewName = "") class FunctionOverviewMenu{ }
        @UxMenu("字典对比查询")@UxRoute(model= DictionaryOverview.MODEL_MODEL,viewType= ViewTypeEnum.FORM,viewName = "") class DictionaryOverviewMenu{ }
        @UxMenu("扩展点对比查询")@UxRoute(model= ExtPointOverview.MODEL_MODEL,viewType= ViewTypeEnum.FORM,viewName = "") class ExtPointOverviewMenu{ }
    }
}