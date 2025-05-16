package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.JSON;

@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 17)
@Base
@Model.model(Menu.MODEL_MODEL)
@Model.Advanced(unique = "name,module", index = {"actionName", "module,show"}, priority = 30)
@Model(displayName = "菜单", summary = "菜单", labelFields = {"displayName"})
public class Menu extends MetaBaseModel {

    private static final long serialVersionUID = -2033983690843948769L;

    public static final String MODEL_MODEL = "base.Menu";

    public static final String NULL_VALUE = "__null__";

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultDisplayName"})
    @Field(displayName = "显示名称", translate = true, required = true, store = NullableBoolEnum.TRUE)
    private String displayName;

    @Base
    @Field(displayName = "默认显示名称")
    private String defaultDisplayName;

    @Base
    @Field(displayName = "api名称", required = true)
    private String name;

    @Base
    @Field.Relation(relationFields = "module")
    @Field(displayName = "模块", required = true)
    private ModuleDefinition moduleDefinition;

    @Base
    @Field(displayName = "模块编码", required = true, invisible = true)
    private String module;

    @Base
    @Field.Enum
    @Field(displayName = "动作类型", required = true)
    private ActionTypeEnum actionType;

    @Base
    @Field(invisible = true)
    private String model;

    @Base
    @Field.String
    @Field(invisible = true)
    private String actionName;

    @Base
    @Field.String(size = 1024)
    @Field(displayName = "数据映射", serialize = JSON, summary = "数据映射")
    private Map<String, Object> mapping;

    @Base
    @Field.String(size = 1024)
    @Field(displayName = "上下文", serialize = JSON, summary = "上下文")
    private Map<String, Object> context;

    @Base
    @Field.Relation(relationFields = {"model", "actionName"}, referenceFields = {"model", "name"}, domain = "actionType=eq=VIEW")
    @Field.many2one
    @Field(displayName = "窗口动作")
    private ViewAction viewAction;

    @Base
    @Field.Relation(relationFields = {"model", "actionName"}, referenceFields = {"model", "name"}, domain = "actionType=eq=SERVER")
    @Field.many2one
    @Field(displayName = "服务器动作")
    private ServerAction serverAction;

    @Base
    @Field.Relation(relationFields = {"model", "actionName"}, referenceFields = {"model", "name"}, domain = "actionType=eq=URL")
    @Field.many2one
    @Field(displayName = "URL动作")
    private UrlAction urlAction;

    @Base
    @Field.Relation(relationFields = {"model", "actionName"}, referenceFields = {"model", "name"}, domain = "actionType=eq=CLIENT")
    @Field.many2one
    @Field(displayName = "客户端动作")
    private ClientAction clientAction;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultIcon"})
    @Field(displayName = "图标", store = NullableBoolEnum.TRUE)
    private String icon;

    @Base
    @Field(displayName = "默认图标")
    private String defaultIcon;

    @Base
    @Field(displayName = "提示信息")
    private String hint;

    @Base
    @Field(displayName = "菜单说明")
    private String description;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultPriority"})
    @Field(displayName = "优先级", store = NullableBoolEnum.TRUE)
    private Long priority;

    @Base
    @Field(displayName = "默认图标")
    private Long defaultPriority;

    @Base
    @Field.Relation(relationFields = {"module", "parentName"}, referenceFields = {"module", "name"})
    @Field(displayName = "上级菜单")
    private Menu parent;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultParentName"})
    @Field(invisible = true, store = NullableBoolEnum.TRUE)
    private String parentName;

    @Base
    @Field(displayName = "默认上级菜单名称")
    private String defaultParentName;

    @Base
    @Field.Relation(relationFields = {"module", "name"}, referenceFields = {"module", "parentName"})
    @Field(displayName = "子菜单")
    private List<Menu> children;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultShow"})
    @Field(displayName = "是否显示", defaultValue = "true", required = true, store = NullableBoolEnum.TRUE)
    private ActiveEnum show;

    @Base
    @Field(displayName = "默认是否显示", defaultValue = "true", required = true)
    private ActiveEnum defaultShow;

    @Field.Enum
    @Field(displayName = "应用支持的客户端类型列表", multi = true, defaultValue = "3")
    private List<ClientTypeEnum> clientTypes;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    public String getDisplayName() {
        String displayName = (String) _d.get("displayName");
        if (displayName == null) {
            displayName = (String) _d.get("defaultDisplayName");
        }
        return displayName;
    }

    public String getIcon() {
        String icon = (String) _d.get("icon");
        if (icon == null) {
            icon = (String) _d.get("defaultIcon");
        }
        return icon;
    }

    public Long getPriority() {
        Long priority = (Long) _d.get("priority");
        if (priority == null) {
            priority = (Long) _d.get("defaultPriority");
        }
        return priority;
    }

    public String getParentName() {
        String parentName = (String) _d.get("parentName");
        if (parentName == null) {
            return (String) _d.get("defaultParentName");
        } else if (NULL_VALUE.equals(parentName)) {
            return null;
        }
        return parentName;
    }

    public String getOriginParentName() {
        String parentName = (String) _d.get("parentName");
        if (parentName == null) {
            parentName = (String) _d.get("defaultParentName");
        }
        return parentName;
    }

    public ActiveEnum getShow() {
        ActiveEnum show = (ActiveEnum) _d.get("show");
        if (show == null) {
            show = (ActiveEnum) _d.get("defaultShow");
        }
        return show;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "moduleDefinition", "parent", "children",
                "viewAction", "clientAction", "serverAction", "urlAction",
                "displayName", "icon", "priority", "parentName", "show"
        );
    }

    public static String sign(String module, String name) {
        return module + CharacterConstants.SEPARATOR_OCTOTHORPE + name;
    }

}
