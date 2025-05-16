package pro.shushi.pamirs.apps.core.action;

import pro.shushi.pamirs.apps.api.tmodel.AppMenu;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author haibo(xf.z@shushi.pro)
 * @date 2022-11-18 14:42:40
 */
@Base
@Model.model(AppMenu.MODEL_MODEL)
public class AppMenuAction {

    @Function(openLevel = FunctionOpenEnum.API, summary = " 查询单个菜单树")
    @Function.Advanced(displayName = "查询单个菜单树", type = FunctionTypeEnum.QUERY)
    public List<AppMenu> fetchSingleMenuTree(Long menuId) {
        if(menuId == null) return new ArrayList<>();
        List<AppMenu> list = new ArrayList<>();
        AppMenu current = new AppMenu().queryById(menuId);
        list.add(current);
        String parentName = Optional.ofNullable(current.getParent()).map(Menu::getName).orElse(null);
        while (parentName != null){
            AppMenu parent = new AppMenu().setName(parentName).queryOne();
            if(parent != null) list.add(parent);
            parentName = Optional.ofNullable(parent).map(AppMenu::getParent).map(Menu::getName).orElse(null);
        }
        return list;
    }
}
