package pro.shushi.pamirs.translate.service;

import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;

/**
 * 翻译解析上下文
 *
 * @author Adamancy Zhang at 12:01 on 2024-01-17
 */
public class TranslateCompileContext {

    private ViewAction currentViewAction;

    private UIView currentView;

    private UIField currentField;

    private String currentModel;
    private String currentModule;

    private boolean isMainView;

    private boolean tableEditable;

    public TranslateCompileContext(String module) {
        this.currentModule = module;
    }

    public TranslateCompileContext() {
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

    public String getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(String currentModule) {
        this.currentModule = currentModule;
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
