package pro.shushi.pamirs.sys.setting.auth;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeLoadExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthNodeHelper;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.boot.base.constants.SystemConfigKeyConstants;
import pro.shushi.pamirs.boot.base.enmu.BindingTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.proxy.AdvancedHomeUeModuleProxy;
import pro.shushi.pamirs.boot.base.tmodel.AdvancedHomePageConfig;
import pro.shushi.pamirs.boot.base.tmodel.HomePageConfigRules;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Wuxin
 * @Date 2024/7/3
 * @since 1.0
 */
@Component
@Order(88)
public class AdvancedHomePageNodeLoadExtend implements PermissionNodeLoadExtendApi {

    @Autowired
    private AppConfigService appConfigService;

    @Resource
    private MetaCacheManager metaCacheManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<PermissionNode> buildRootPermissions(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
        AppConfig globalConfig = appConfigService.fetchGlobalConfig();
        if (globalConfig == null) {
            return null;
        }

        List<HomePageConfigRules> rules = Optional.ofNullable(globalConfig.getExtend())
                .map(v -> v.get(SystemConfigKeyConstants.ADVANCED_HOME_PAGE))
                .map(v -> JsonUtils.parseMap2Object((Map<String, Object>) v, AdvancedHomePageConfig.class))
                .filter(v -> Boolean.TRUE.equals(v.getState()))
                .map(AdvancedHomePageConfig::getRules)
                .orElse(null);
        if (CollectionUtils.isEmpty(rules)) {
            return null;
        }

        Map<String, ModulePermissionNode> moduleNodes = new HashMap<>();
        for (PermissionNode node : nodes) {
            if (node instanceof ModulePermissionNode) {
                ModulePermissionNode moduleNode = (ModulePermissionNode) node;
                String module = moduleNode.getModule();
                moduleNodes.put(module, moduleNode);
            }
        }

        List<PermissionNode> newNodes = new ArrayList<>();
        Map<String, PermissionNode> roots = new HashMap<>();
        for (HomePageConfigRules rule : rules) {
            View view = validateRule(rule);
            if (view == null) {
                continue;
            }

            String module = rule.getBindHomePageModule().getModule();
            ModulePermissionNode moduleNode = moduleNodes.get(module);
            if (moduleNode == null) {
                continue;
            }

            ViewAction viewAction = null;
            Action action = metaCacheManager.fetchAction(view.getModel(), rule.getCode());
            if (action instanceof ViewAction) {
                viewAction = (ViewAction) action;
            }
            if (viewAction == null) {
                continue;
            }

            PermissionNode root = roots.computeIfAbsent(module, k -> {
                PermissionNode homepageNode = createAdvancedHomePageNode(moduleNode);
                List<PermissionNode> currentNodes = moduleNode.getNodes();
                if (CollectionUtils.isEmpty(currentNodes)) {
                    currentNodes.add(homepageNode);
                } else {
                    PermissionNode permissionNode = currentNodes.get(0);
                    if (permissionNode != null && ResourcePermissionSubtypeEnum.HOMEPAGE.equals(permissionNode.getNodeType())) {
                        currentNodes.add(1, homepageNode);
                    } else {
                        currentNodes.add(0, homepageNode);
                    }
                }
                return homepageNode;
            });
            AuthNodeHelper.addNode(newNodes, root, AuthNodeHelper.createViewActionNode(module, viewAction, root));
        }
        return newNodes;
    }

    private static View validateRule(HomePageConfigRules rule) {
        if (Boolean.FALSE.equals(rule.getEnabled())) {
            return null;
        }
        AdvancedHomeUeModuleProxy ueModule = rule.getBindHomePageModule();
        if (ueModule == null || StringUtils.isBlank(ueModule.getModule())) {
            return null;
        }
        if (BindingTypeEnum.MENU.equals(rule.getBindingType())) {
            return null;
        }
        if (StringUtils.isEmpty(rule.getRuleName())) {
            return null;
        }
        if (StringUtils.isBlank(rule.getCode())) {
            return null;
        }
        View view = rule.getBindHomePageView();
        if (view == null || view.getModel() == null || view.getName() == null) {
            return null;
        }
        return view;
    }

    private PermissionNode createAdvancedHomePageNode(PermissionNode parent) {
        return AuthNodeHelper.createNodeWithTranslate("AdvancedHomePage", "高级首页配置", parent);
    }

}
