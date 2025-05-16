package pro.shushi.pamirs.auth.api.loader.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeConvertExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthEnumerationHelper;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoaderConstants;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeConverter;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodePathGenerator;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.core.common.TranslateUtils;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 默认资源权限节点转换
 *
 * @author Adamancy Zhang at 13:15 on 2024-02-04
 */
@Component(AuthConstants.RESOURCE_PERMISSION_NODE_CONVERTER_BEAN_NAME)
public class DefaultResourcePermissionNodeConverter implements ResourcePermissionNodeConverter {

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Override
    public ModulePermissionNode convertModuleNode(UeModule module) {
        Long moduleId = module.getId();
        String moduleModule = module.getModule();

        ModulePermissionNode node = new ModulePermissionNode();
        node.setId(moduleId.toString());
        node.setHasNext(Boolean.FALSE);
        node.setCanAllot(Boolean.FALSE);
        node.setNodeType(ResourcePermissionSubtypeEnum.MODULE);
        node.setResourceId(moduleId);
        node.setDisplayValue(TranslateUtils.translateValues(module.getDisplayName()));
        node.setModule(moduleModule);
        node.setIcon(module.getLogo());
        node.setPath(getPathGenerator().generatorModulePath(node));

        extendConvert((extendApi) -> extendApi.convertModuleNode(node, module));
        return node;
    }

    @Override
    public HomepagePermissionNode convertHomepageNode(UeModule module) {
        Long moduleId = module.getId();
        String moduleModule = module.getModule();

        HomepagePermissionNode node = new HomepagePermissionNode();
        node.setId(moduleId.toString() + PermissionNodeLoaderConstants.HOMEPAGE_ID_SUFFIX);
        node.setHasNext(Boolean.FALSE);
        node.setCanAllot(Boolean.FALSE);
        node.setNodeType(ResourcePermissionSubtypeEnum.HOMEPAGE);
        node.setResourceId(moduleId);
        node.setDisplayValue(TranslateUtils.translateValues(PermissionNodeLoaderConstants.HOMEPAGE_DISPLAY_VALUE));
        node.setModule(moduleModule);
        node.setPath(getPathGenerator().generatorHomepagePath(node));

        extendConvert((extendApi) -> extendApi.convertHomepageNode(node, module));
        return node;
    }

    @Override
    public HomepagePermissionNode convertHomepageNode(UeModule module, ViewAction action) {
        HomepagePermissionNode node = convertHomepageNode(module);
        node.setModel(action.getModel());
        node.setAction(action.getName());
        node.setCanAllot(Boolean.TRUE);

        extendConvert((extendApi) -> extendApi.convertHomepageNode(node, module, action));
        return node;
    }

    @Override
    public MenuPermissionNode convertMenuNode(Menu menu) {
        Long menuId = menu.getId();
        String module = menu.getModule();

        MenuPermissionNode node = new MenuPermissionNode();
        node.setId(menuId.toString());
        node.setHasNext(Boolean.FALSE);
        node.setNodeType(ResourcePermissionSubtypeEnum.MENU);
        node.setResourceId(menuId);
        node.setDisplayValue(TranslateUtils.translateValues(menu.getDisplayName()));
        node.setModule(module);
        node.setName(menu.getName());
        node.setPriority(menu.getPriority());

        String actionName = menu.getActionName();
        if (StringUtils.isBlank(actionName)) {
            node.setCanAllot(Boolean.FALSE);
        } else {
            node.setCanAllot(Boolean.TRUE);
            node.setModel(menu.getModel());
            node.setAction(actionName);
        }

        node.setPath(getPathGenerator().generatorMenuPath(node));

        extendConvert((extendApi) -> extendApi.convertMenuNode(node, menu));
        return node;
    }

    @Override
    public ActionPermissionNode convertActionNode(AuthCompileContext context, UIAction actionNode, Action action) {
        Long actionId = action.getId();
        String model = action.getModel();
        String actionName = action.getName();
        ViewAction currentViewAction = context.getCurrentViewAction();

        ActionPermissionNode node = new ActionPermissionNode();
        node.setId(actionId.toString());
        node.setParentId(currentViewAction.getId().toString());
        node.setHasNext(Boolean.FALSE);
        node.setCanAllot(Boolean.TRUE);
        node.setNodeType(AuthEnumerationHelper.getActionResourceSubtype(action.getActionType()));
        node.setResourceId(actionId);
        node.setDisplayValue(AuthHelper.getActionDisplayValue(action, actionNode));
        node.setNodes(new ArrayList<>());
        node.setModule(context.getInfo().getModule());
        node.setModel(model);
        node.setAction(actionName);
        node.setActionType(action.getActionType());
        String menuName = currentViewAction.getDisplayName();
        if (StringUtils.isNotBlank(menuName) && menuName.endsWith(CommonConstants.LOW_CODE_HOMEPAGE_SUFFIX)) {
            menuName = CommonConstants.HOMEPAGE_DISPLAY_NAME;
        }
        node.setMenuName(menuName);

        String path = getPathGenerator().generatorActionPath(context, node);
        node.setPath(path);
        node.setCanAccess(isAccessAction(context, path));

        AccessResourceInfo lastInfo = context.getInfo();
        lastInfo.addActionPath(model, actionName);
        extendConvert((extendApi) -> extendApi.convertActionNode(node, context, actionNode, action));
        lastInfo.getPaths().remove(lastInfo.getPaths().size() - 1);
        return node;
    }

    @Override
    public ActionPermissionNode buildAllActionNode(AuthCompileContext context) {
        ViewAction currentViewAction = context.getInfo().getViewAction();

        ActionPermissionNode node = new ActionPermissionNode();
        node.setId(AuthConstants.ALL_FLAG_LONG.toString());
        node.setParentId(currentViewAction.getId().toString());
        node.setHasNext(Boolean.FALSE);
        node.setCanAllot(Boolean.TRUE);
        node.setResourceId(AuthConstants.ALL_FLAG_LONG);
        node.setDisplayValue(AuthConstants.ALL_FLAG_DISPLAY_NAME);
        node.setMenuName(currentViewAction.getDisplayName());
        node.setAction("");
        String path = getPathGenerator().generatorAllActionPath(context, node);
        if (StringUtils.isBlank(path)) {
            return null;
        }
        node.setPath(path);
        node.setCanAccess(isAccessAction(context, path));
        return node;
    }

    protected boolean isAccessAction(AuthCompileContext context, String path) {
        Map<String, AuthResourceAuthorization> authorizationMap = context.getAuthorizationMap();
        if (authorizationMap == null) {
            return false;
        }
        Long authorizedValue = Optional.ofNullable(authorizationMap.get(path))
                .map(AuthResourceAuthorization::getAuthorizedValue)
                .orElse(null);
        if (authorizedValue == null) {
            return false;
        }
        return ResourceAuthorizedValueEnum.isAccess(authorizedValue);
    }

    protected ResourcePermissionNodePathGenerator getPathGenerator() {
        return permissionNodeLoader.getPathGenerator();
    }

    protected void extendConvert(Consumer<PermissionNodeConvertExtendApi> consumer) {
        for (PermissionNodeConvertExtendApi extendApi : Spider.getLoader(PermissionNodeConvertExtendApi.class).getOrderedExtensions()) {
            consumer.accept(extendApi);
        }
    }
}
