package pro.shushi.pamirs.auth.api.loader.visitor;

import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;

import java.util.Map;

/**
 * 权限编译上下文
 *
 * @author Adamancy Zhang at 12:01 on 2024-01-17
 */
public class AuthCompileContext {

    private final AccessResourceInfo info;

    private final Map<String, AuthResourceAuthorization> authorizationMap;

    private ActionPermissionNode node;

    private ViewAction currentViewAction;

    private UIView currentView;

    private UIField currentField;

    private String currentModel;

    private boolean isMainView;

    private boolean tableEditable;

    public AuthCompileContext(AccessResourceInfo info, Map<String, AuthResourceAuthorization> authorizationMap) {
        this.info = info;
        this.authorizationMap = authorizationMap;
        this.isMainView = false;
        this.tableEditable = false;
    }

    public AccessResourceInfo getInfo() {
        return info;
    }

    public Map<String, AuthResourceAuthorization> getAuthorizationMap() {
        return authorizationMap;
    }

    public ActionPermissionNode getNode() {
        return node;
    }

    public void setNode(ActionPermissionNode node) {
        this.node = node;
    }

    public ViewAction getRootViewAction() {
        return this.getInfo().getViewAction();
    }

    public ViewAction getCurrentViewAction() {
        return currentViewAction;
    }

    public void setCurrentViewAction(ViewAction currentViewAction) {
        this.currentViewAction = currentViewAction;
    }

    public UIView getCurrentView() {
        return currentView;
    }

    public void setCurrentView(UIView currentView) {
        this.currentView = currentView;
    }

    public UIField getCurrentField() {
        return currentField;
    }

    public void setCurrentField(UIField currentField) {
        this.currentField = currentField;
    }

    public String getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(String currentModel) {
        this.currentModel = currentModel;
    }

    public boolean isMainView() {
        return isMainView;
    }

    public void setIsMainView(boolean isMainView) {
        this.isMainView = isMainView;
    }

    public boolean getTableEditable() {
        return tableEditable;
    }

    public void setTableEditable(boolean tableEditable) {
        this.tableEditable = tableEditable;
    }
}
