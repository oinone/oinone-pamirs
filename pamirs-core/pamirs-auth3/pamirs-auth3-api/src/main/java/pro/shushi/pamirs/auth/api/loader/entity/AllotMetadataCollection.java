package pro.shushi.pamirs.auth.api.loader.entity;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.core.common.ObjectHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可分配元数据集合
 *
 * @author Adamancy Zhang at 11:00 on 2024-09-13
 */
public class AllotMetadataCollection {

    private final List<UeModule> allotModules;

    private final Set<String> allotModuleModules;

    private final Map<String, ViewAction> allotModuleHomepages;

    private final Map<String, List<Menu>> allotModuleMenus;

    private List<Menu> validMenus = new ArrayList<>();

    private final Set<String> repeatActionSet = new HashSet<>();

    private final List<String> actionModels = new ArrayList<>();

    private final List<String> actionNames = new ArrayList<>();

    public AllotMetadataCollection() {
        this.allotModules = Collections.emptyList();
        this.allotModuleModules = Collections.emptySet();
        this.allotModuleHomepages = Collections.emptyMap();
        this.allotModuleMenus = Collections.emptyMap();
    }

    public AllotMetadataCollection(List<UeModule> allotModules,
                                   Map<String, ViewAction> allotModuleHomepages,
                                   Map<String, List<Menu>> allotModuleMenus) {
        this.allotModules = allotModules;
        this.allotModuleModules = allotModules.stream().map(UeModule::getModule).collect(Collectors.toSet());
        this.allotModuleHomepages = allotModuleHomepages;
        this.allotModuleMenus = allotModuleMenus;
    }

    public List<UeModule> getAllotModules() {
        return allotModules;
    }

    public Set<String> getAllotModuleModules() {
        return allotModuleModules;
    }

    public Map<String, ViewAction> getAllotModuleHomepages() {
        return allotModuleHomepages;
    }

    public Map<String, List<Menu>> getAllotModuleMenus() {
        return allotModuleMenus;
    }

    public List<Menu> getValidMenus() {
        return validMenus;
    }

    public void setValidMenus(List<Menu> validMenus) {
        this.validMenus = validMenus;
    }

    public Set<String> getRepeatActionSet() {
        return repeatActionSet;
    }

    public List<String> getActionModels() {
        return actionModels;
    }

    public List<String> getActionNames() {
        return actionNames;
    }

    public void addValidMenu(Menu menu) {
        validMenus.add(menu);
        String model = menu.getModel();
        String actionName = menu.getActionName();
        String sign = Action.sign(model, actionName);
        if (ObjectHelper.isNotRepeat(repeatActionSet, sign)) {
            actionModels.add(model);
            actionNames.add(actionName);
        }
    }

    public void addValidViewAction(ViewAction viewAction) {
        String model = viewAction.getModel();
        String actionName = viewAction.getName();
        String sign = Action.sign(model, actionName);
        if (ObjectHelper.isNotRepeat(repeatActionSet, sign)) {
            actionModels.add(model);
            actionNames.add(actionName);
        }
    }
}