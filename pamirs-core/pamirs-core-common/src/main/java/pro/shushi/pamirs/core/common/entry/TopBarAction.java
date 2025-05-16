package pro.shushi.pamirs.core.common.entry;

import com.google.common.collect.Lists;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.core.common.enmu.TopBarActionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 顶部栏动作
 *
 * @author Adamancy Zhang at 15:44 on 2024-02-28
 */
public class TopBarAction {

    public static final String PamirsUserTransient_MODEL_MODEL = "user.PamirsUserTransient";

    public static final String LOGOUT_ACTION = "logout";

    private final String module;

    private final String model;

    private final String name;

    private final ActionTypeEnum actionType;

    private String displayName;

    private TopBarActionType type;

    private String icon;

    private String sessionPath;

    /**
     * 运行时根据{@link TopBarAction#sessionPath}自动解析，设置无效
     */
    private AccessResourceInfo info;

    /**
     * 不在系统权限节点中显示，权限通过其他权限进行配置和校验
     * <p>
     * 如:
     * 【个人设置】通过设置【个人中心】-【个人设置】的访问权限进行控制，不单独配置
     * </p>
     */
    private Boolean authIgnored;

    /**
     * 分组，升序排列。同一分组按插入顺序排列。普通类型动作必填。
     */
    private Integer groupOrder;

    /**
     * 备选动作，当前动作没有权限时，从备选动作中按顺序取出有权限的一个动作作为当前动作
     */
    private List<TopBarAction> alternates;

    public TopBarAction(@NotBlank String module, @NotBlank String model, @NotBlank String name, @NotNull ActionTypeEnum actionType) {
        this.module = module;
        this.model = model;
        this.name = name;
        this.actionType = actionType;
    }

    public TopBarAction(@NotBlank String module, @NotBlank String model, @NotBlank String name, @NotNull ActionTypeEnum actionType, Integer groupOrder) {
        this.module = module;
        this.model = model;
        this.name = name;
        this.actionType = actionType;
        this.groupOrder = groupOrder;
    }

    public String getModule() {
        return module;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TopBarAction setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public TopBarActionType getType() {
        return type;
    }

    public TopBarAction setType(TopBarActionType type) {
        this.type = type;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public TopBarAction setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getSessionPath() {
        return sessionPath;
    }

    public TopBarAction setSessionPath(String sessionPath) {
        this.sessionPath = sessionPath;
        return this;
    }

    public TopBarAction setDefaultSessionPath() {
        this.sessionPath = ResourcePath.generatorPath(getModule(), getName());
        return this;
    }

    public AccessResourceInfo getInfo() {
        return info;
    }

    public TopBarAction setInfo(AccessResourceInfo info) {
        this.info = info;
        return this;
    }

    public Boolean getAuthIgnored() {
        return authIgnored;
    }

    public TopBarAction setAuthIgnored(Boolean authIgnored) {
        this.authIgnored = authIgnored;
        return this;
    }

    public Integer getGroupOrder() {
        return groupOrder;
    }

    public TopBarAction setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
        return this;
    }

    public List<TopBarAction> getAlternates() {
        return alternates;
    }

    public TopBarAction setAlternates(List<TopBarAction> alternates) {
        this.alternates = alternates;
        return this;
    }

    public static List<TopBarAction> getDefaultActions() {
        List<TopBarAction> list = new ArrayList<>();

        // 个人中心
        list.add(new TopBarAction("my_center", "my.MyPamirsUserProxy", "MyCenterMenus_SettingMenu", ActionTypeEnum.VIEW)
                .setType(TopBarActionType.USER_AVATAR)
                .setAuthIgnored(Boolean.TRUE)
                .setDefaultSessionPath());

        // 系统设置
        list.add(new TopBarAction("sys_setting", "sysSetting.GlobalAppConfigProxy", "SysSettingMenus_GlobalMenu_LoginMenu", ActionTypeEnum.VIEW, 1)
                .setDisplayName("系统设置")
                .setIcon("oinone-xitongpeizhi")
                .setAuthIgnored(Boolean.TRUE)
                .setDefaultSessionPath()
                .setAlternates(Lists.newArrayList(
                        new TopBarAction("sys_setting", "sysSetting.GlobalAppConfigProxy", "SysSettingMenus_GlobalMenu_CorporateImageMenu", ActionTypeEnum.VIEW).setDefaultSessionPath(),
                        new TopBarAction("sys_setting", "sysSetting.GlobalAppConfigProxy", "SysSettingMenus_GlobalMenu_SysStyleMenu", ActionTypeEnum.VIEW).setDefaultSessionPath()
                )));

        // 工作台
        list.add(new TopBarAction("workbench", "workbench.WorkBenchHomePage", "homepage", ActionTypeEnum.VIEW, 1)
                .setDisplayName("工作台")
                .setIcon("oinone-gongzuotai")
                .setAuthIgnored(Boolean.TRUE)
                .setDefaultSessionPath());

        // 修改密码
        list.add(new TopBarAction("user", PamirsUserTransient_MODEL_MODEL, "modify_pwd_view_action", ActionTypeEnum.VIEW, 1)
                .setIcon("oinone-xiugaimima"));

        // 登出
        list.add(new TopBarAction("user", PamirsUserTransient_MODEL_MODEL, LOGOUT_ACTION, ActionTypeEnum.SERVER, 2)
                .setIcon("oinone-tuichu")
                .setAuthIgnored(Boolean.TRUE));

        return list;
    }
}
