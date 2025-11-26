package pro.shushi.pamirs.management.center.init;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.TreeHelper;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.management.center.ManagementCenterModule;
import pro.shushi.pamirs.management.center.tmodel.RoleUserManager;
import pro.shushi.pamirs.management.center.tmodel.UserBindingManager;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.*;

/**
 * {@link ManagementCenterModule}元数据编辑
 *
 * @author Adamancy Zhang at 20:08 on 2024-02-19
 */
@Order
@Component
public class ManagementCenterMetadataEdit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, ManagementCenterModule.MODULE_MODULE, ManagementCenterModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        viewActionInit(util);
        UeModule currentModule = util.getUeModule();
        if (StringUtils.isNoneBlank(currentModule.getHomePageModel(), currentModule.getHomePageName())) {
            return;
        }
        List<Menu> allMenus = new ArrayList<>();
        for (Meta meta : metaMap.values()) {
            Collection<Menu> moduleMenus = Optional.ofNullable(meta.getCurrentModuleData())
                    .map(v -> v.<Menu>getDataMap(Menu.MODEL_MODEL))
                    .map(Map::values)
                    .orElse(null);
            if (CollectionUtils.isNotEmpty(moduleMenus)) {
                moduleMenus.stream().filter(v -> ManagementCenterModule.MODULE_MODULE.equals(v.getModule())).forEach(allMenus::add);
            }
        }
        if (CollectionUtils.isEmpty(allMenus)) {
            return;
        }
        List<TreeNode<Menu>> roots = TreeHelper.convert(allMenus, Menu::getName, Menu::getParentName);
        roots.sort(Comparator.comparing(v -> v.getValue().getPriority()));
        Menu homepageMenu = findHighPriorityMenu(roots);
        if (homepageMenu != null) {
            util.setHomepageByMenu(homepageMenu);
        }
    }

    private Menu findHighPriorityMenu(List<TreeNode<Menu>> nodes) {
        for (TreeNode<Menu> node : nodes) {
            node.sortChildren(Comparator.comparing(v -> v.getValue().getPriority()));
            Menu lasted = findHighPriorityMenu(node.getChildren());
            if (lasted != null) {
                return lasted;
            }
            Menu menu = node.getValue();
            ViewAction viewAction = menu.getViewAction();
            String homepageModel, homepageName;
            if (viewAction == null) {
                homepageModel = menu.getModel();
                homepageName = menu.getActionName();
            } else {
                homepageModel = viewAction.getModel();
                homepageName = viewAction.getName();
            }
            if (StringUtils.isAllBlank(homepageModel, homepageName)) {
                continue;
            }
            return node.getValue();
        }
        return null;
    }

    private void viewActionInit(InitializationUtil util) {
        util.createViewAction("userManager",
                "用户管理",
                AuthRole.MODEL_MODEL,
                Lists.newArrayList(ViewTypeEnum.TABLE),
                RoleUserManager.MODEL_MODEL,
                ViewTypeEnum.FORM,
                ActionContextTypeEnum.SINGLE,
                ActionTargetEnum.DIALOG,
                "roleUserManagerForm");


        util.createViewAction("bingingUser",
                "绑定用户",
                AuthRole.MODEL_MODEL,
                Lists.newArrayList(ViewTypeEnum.TABLE),
                UserBindingManager.MODEL_MODEL,
                ViewTypeEnum.FORM,
                ActionContextTypeEnum.SINGLE_AND_BATCH,
                ActionTargetEnum.DIALOG,
                "userBindingManagerFrom");
    }
}