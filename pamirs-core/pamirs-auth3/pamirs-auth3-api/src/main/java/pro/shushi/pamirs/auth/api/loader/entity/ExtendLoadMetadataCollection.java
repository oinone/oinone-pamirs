package pro.shushi.pamirs.auth.api.loader.entity;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.core.common.ObjectHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 扩展加载元数据集合
 *
 * @author Adamancy Zhang at 19:56 on 2024-10-08
 */
public class ExtendLoadMetadataCollection {

    /**
     * 已加载动作权限集合
     */
    private final Set<String> loadedActionPermissions;

    private final List<String> actionModels = new ArrayList<>();

    private final List<String> actionNames = new ArrayList<>();

    public ExtendLoadMetadataCollection(Set<String> loadedActionPermissions) {
        this.loadedActionPermissions = loadedActionPermissions;
    }

    public Set<String> getLoadedActionPermissions() {
        return loadedActionPermissions;
    }

    public List<String> getActionModels() {
        return actionModels;
    }

    public List<String> getActionNames() {
        return actionNames;
    }

    public void addAction(String model, String actionName) {
        String sign = Action.sign(model, actionName);
        if (ObjectHelper.isNotRepeat(loadedActionPermissions, sign)) {
            actionModels.add(model);
            actionNames.add(actionName);
        }
    }

    public void clear() {
        this.actionModels.clear();
        this.actionNames.clear();
    }
}
