package pro.shushi.pamirs.apps.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.tmodel.AppCategoryModule;
import pro.shushi.pamirs.apps.api.tmodel.AppCategoryModuleList;
import pro.shushi.pamirs.apps.view.manager.AppCategoryModuleManager;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

/**
 * AppCategoryModuleAction
 *
 * @author yakir on 2022/11/28 20:22.
 */
@Base
@Component
@Model.model(AppCategoryModule.MODEL_MODEL)
public class AppCategoryModuleAction {

    @Autowired
    private AppCategoryModuleManager appCategoryModuleManager;

    @Action(displayName = "应用列表")
    @Action.Advanced(type = FunctionTypeEnum.QUERY)
    public List<AppCategoryModuleList> appModuleList() {
        return appCategoryModuleManager.categoryModules();
    }

}
