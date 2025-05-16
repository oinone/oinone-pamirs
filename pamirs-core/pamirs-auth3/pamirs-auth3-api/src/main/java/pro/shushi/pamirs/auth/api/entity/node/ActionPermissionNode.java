package pro.shushi.pamirs.auth.api.entity.node;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;

/**
 * 动作权限节点
 *
 * @author Adamancy Zhang at 14:20 on 2024-01-15
 */
public class ActionPermissionNode extends PermissionNode {

    private static final long serialVersionUID = 4072949186796299730L;

    private String module;

    private String model;

    private String action;

    @JSONField(serialize = false)
    private ActionTypeEnum actionType;

    private String menuName;

    private Boolean ignoreChildren;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Boolean getIgnoreChildren() {
        return ignoreChildren;
    }

    public void setIgnoreChildren(Boolean ignoreChildren) {
        this.ignoreChildren = ignoreChildren;
    }
}
