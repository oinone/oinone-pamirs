package pro.shushi.pamirs.boot.web.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 页面加载帮助类
 *
 * @author Adamancy Zhang at 11:16 on 2023-12-23
 */
public class PageLoadHelper {

    /**
     * 获取当前客户端类型
     *
     * @return 当前客户端类型
     */
    public static ClientTypeEnum getCurrentClientType() {
        ClientTypeEnum clientType = PamirsSession.getRequestVariables().getClientType();
        if (clientType == null) {
            return ClientTypeEnum.PC;
        }
        return clientType;
    }

    /**
     * 判断指定模块是否在当前客户端可用
     *
     * @param moduleDefinition 模块定义
     * @return 是否在当前客户端可用
     */
    public static boolean isCurrentClientApplication(ModuleDefinition moduleDefinition) {
        if (Boolean.TRUE.equals(moduleDefinition.getApplication())) {
            List<ClientTypeEnum> clientTypes = moduleDefinition.getClientTypes();
            if (CollectionUtils.isNotEmpty(clientTypes)) {
                return clientTypes.contains(getCurrentClientType());
            }
        }
        return false;
    }

    /**
     * 获取当前加载页面模块
     *
     * @param model 当前加载页面模型
     * @return 当前加载页面模块
     */
    public static ModuleDefinition getPageLoadModule(String model) {
        String sessionModule = Optional.ofNullable(AccessResourceInfoSession.getInfo())
                .filter(v -> !v.isFixed())
                .map(AccessResourceInfo::getModule)
                .orElse(null);
        String moduleName = PamirsSession.getRequestVariables().getHeaders().get(VariableNameConstants.module);
        if (StringUtils.isNotBlank(moduleName)) {
            ModuleDefinition headerModule = PamirsSession.getContext().getModuleCache().getByName(moduleName);
            if (headerModule == null) {
                return null;
            }
            if (StringUtils.isNotBlank(sessionModule) && !sessionModule.equals(headerModule.getModule())) {
                return PamirsSession.getContext().getModule(sessionModule);
            }
            return headerModule;
        }
        String module = Optional.ofNullable(model).filter(StringUtils::isNotBlank).map(v -> PamirsSession.getContext().getModelConfig(v)).map(ModelConfig::getModule).orElse(null);
        if (StringUtils.isBlank(module)) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_MODULE_CAN_NOT_ACCESS_ERROR).errThrow();
        }
        return PamirsSession.getContext().getModule(module);
    }

    public static List<Menu> fetchValidMenus(String module) {
        List<Menu> menus = PageLoadHelper.fetchMenus(module);
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }
        ClientTypeEnum clientType = PageLoadHelper.getCurrentClientType();
        AuthApi authApi = AuthApi.get();
        List<Menu> authMenus = menus.stream().filter(v -> PageLoadHelper.isShowMenu(v, clientType) && authApi.canAccessMenu(module, v.getName()).getSuccess()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(authMenus)) {
            menus = authMenus;
        } else {
            Map<String, Menu> cache = new HashMap<>(menus.size());
            Iterator<Menu> menuIterator = menus.iterator();
            menus = PageLoadHelper.fillParentMenus(cache, menuIterator, authMenus, clientType);
        }
        return menus;
    }

    /**
     * 获取模块菜单列表
     *
     * @param module 指定模块编码
     * @return 指定模块的菜单列表
     */
    public static List<Menu> fetchMenus(String module) {
        return CommonApiFactory.getApi(MetaCacheManager.class).fetchCloneMenus(module);
    }

    public static boolean isShowMenu(Menu menu, ClientTypeEnum clientType) {
        return ActiveEnum.ACTIVE.equals(menu.getShow()) && (CollectionUtils.isEmpty(menu.getClientTypes()) || menu.getClientTypes().contains(clientType));
    }

    public static List<Menu> fillParentMenus(Map<String, Menu> cache, Iterator<Menu> menuIterator, List<Menu> menus, ClientTypeEnum clientType) {
        List<Menu> menuResult = new ArrayList<>();
        Set<String> repeatSet = new HashSet<>(menus.size());
        for (Menu menu : menus) {
            List<Menu> targetMenus = fillParentMenus(cache, menuIterator, clientType, repeatSet, menu);
            for (Menu targetMenu : targetMenus) {
                String targetName = targetMenu.getName();
                if (isRepeat(repeatSet, targetName)) {
                    continue;
                }
                menuResult.add(targetMenu);
            }
        }
        return menuResult;
    }

    private static List<Menu> fillParentMenus(Map<String, Menu> cache, Iterator<Menu> menuIterator, ClientTypeEnum clientType,
                                              Set<String> repeatSet, Menu menu) {
        String parentName = menu.getParentName();
        if (StringUtils.isBlank(parentName)) {
            return Lists.newArrayList(menu);
        }
        Menu parentMenu = getParentMenu(cache, menuIterator, parentName);
        if (parentMenu == null || !PageLoadHelper.isShowMenu(parentMenu, clientType)) {
            return Collections.emptyList();
        }
        List<Menu> result = fillParentMenus(cache, menuIterator, clientType, repeatSet, parentMenu);
        if (result.isEmpty()) {
            return result;
        }
        result.add(menu);
        return result;
    }

    private static Menu getParentMenu(Map<String, Menu> cache, Iterator<Menu> menuIterator, String name) {
        Menu menu = cache.get(name);
        if (menu == null) {
            while (menuIterator.hasNext()) {
                Menu target = menuIterator.next();
                String targetName = target.getName();
                cache.put(targetName, target);
                if (targetName.equals(name)) {
                    return target;
                }
            }
        }
        return menu;
    }

    private static <T> boolean isRepeat(Set<T> traversalSet, T value) {
        Integer traversalSetLastSize = traversalSet.size();
        traversalSet.add(value);
        return traversalSetLastSize.equals(traversalSet.size());
    }

    /**
     * 判断指定动作是否为菜单动作
     *
     * @param module     模块编码
     * @param model      指定动作模型
     * @param actionName 指定动作名称
     * @return 是否为菜单动作
     */
    public static Menu isMenuAction(String module, String model, String actionName) {
        List<Menu> menus = fetchMenus(module);
        if (CollectionUtils.isEmpty(menus)) {
            return null;
        }
        return menus.stream().filter(v -> {
            String menuActionModel = v.getModel();
            String menuActionName = v.getActionName();
            if (StringUtils.isAnyBlank(menuActionModel, menuActionName)) {
                return Boolean.FALSE;
            }
            return menuActionModel.equals(model) && menuActionName.equals(actionName);
        }).findFirst().orElse(null);
    }

    public static <T extends Action> T fillModuleAndModel(T action) {
        // 设置模型
        String model = action.getModel();
        if (StringUtils.isNotBlank(model)) {
            Optional.ofNullable(PamirsSession.getContext().getModelConfig(model))
                    .map(ModelConfig::getModelDefinition)
                    .ifPresent(v -> action.setModelName(v.getName()).setModelDefinition(new ModelDefinition().setModel(model).setName(v.getName()).setType(v.getType())));
        }
        if (action instanceof ViewAction) {
            ViewAction viewAction = (ViewAction) action;
            // 目标模型
            String resModel = viewAction.getResModel();
            if (StringUtils.isNotBlank(resModel)) {
                Optional.ofNullable(PamirsSession.getContext().getModelConfig(resModel))
                        .map(ModelConfig::getModelDefinition).ifPresent(v -> viewAction.setResModelDefinition(
                                new ModelDefinition().setPk(v.getPk()).setModel(resModel).setName(v.getName()).setType(v.getType()))
                        );
            }
            // 设置模块和目标模块
            String module = viewAction.getModule();
            if (StringUtils.isNotBlank(module)) {
                Optional.ofNullable(PamirsSession.getContext().getModule(module))
                        .ifPresent(v -> viewAction.setModuleName(v.getName()).setModuleDefinition(new ModuleDefinition().setModule(module).setName(v.getName())));
            }
            String resModule = viewAction.getResModule();
            String resModuleName = Optional.ofNullable(viewAction.getResModuleDefinition()).map(ModuleDefinition::getName).orElse(null);
            if (StringUtils.isBlank(resModule)) {
                viewAction.setResModule(null).setResModuleName(null)
                        .setResModuleDefinition(null);
            } else if (StringUtils.isBlank(resModuleName)) {
                Optional.ofNullable(PamirsSession.getContext().getModule(resModule))
                        .ifPresent(v -> viewAction.setResModuleName(v.getName()).setResModuleDefinition(new ModuleDefinition().setModule(resModule).setName(v.getName())));
            }
        }
        return action;
    }

    /**
     * 生成当前访问资源信息
     *
     * @param moduleDefinition 当前访问模块
     * @param action           当前访问动作
     * @return 当前访问资源信息
     */
    public static AccessResourceInfo generatorAccessResourceInfo(ModuleDefinition moduleDefinition, Action action) {
        String module = moduleDefinition.getModule();
        String model = action.getModel();
        String actionName = action.getName();

        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        if (info != null) {
            String accessModule = info.getModule();
            if (CollectionUtils.isEmpty(info.getPaths()) &&
                    !info.isFixed() &&
                    (module.equals(accessModule) || info.isActionPath()) &&
                    model.equals(info.getModel()) &&
                    actionName.equals(info.getActionName())) {
                return info;
            }
        }

        Menu targetMenu = findMenuByAction(module, model, actionName);
        if (targetMenu != null) {
            if (AccessResourceInfoSession.isEnabled()) {
                return generatorAccessResourceInfo(module, action, targetMenu);
            }
        }

        String homepageName = moduleDefinition.getHomePageName();
        if (AccessResourceInfoSession.isEnabled()) {
            if (actionName.equals(homepageName)) {
                info = generatorAccessResourceInfo(module, action);
                info.setHomepage(homepageName);
                return info;
            }
            info = AccessResourceInfoSession.getInfo();
            if (info == null) {
                throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_ACTION_NOT_EXIST_ERROR).errThrow();
            }
            if (info.isFixed()) {
                return generatorAccessResourceInfo(module, action);
            }
            info = info.clone();
            ResourcePath lastPath = info.getLastPath();
            if (lastPath == null || !isSameAction(lastPath, model, actionName)) {
                info.addActionPath(model, actionName);
            }
            return info;
        }
        return null;
    }

    private static Menu findMenuByAction(String module, String model, String actionName) {
        List<Menu> menus = fetchMenus(module);
        Menu targetMenu = null;
        if (CollectionUtils.isNotEmpty(menus)) {
            for (Menu menu : menus) {
                String menuActionModel = menu.getModel();
                String menuActionName = menu.getActionName();
                if (StringUtils.isAnyBlank(menuActionModel, menuActionName)) {
                    continue;
                }
                if (model.equals(menuActionModel) && actionName.equals(menuActionName)) {
                    targetMenu = menu;
                }
            }
        }
        return targetMenu;
    }

    private static boolean isSameAction(ResourcePath path, String model, String actionName) {
        return model.equals(path.getModel()) && actionName.equals(path.getName());
    }

    public static AccessResourceInfo generatorAccessResourceInfo(String module, Action action) {
        return generatorAccessResourceInfo(module, action, null);
    }

    private static AccessResourceInfo generatorAccessResourceInfo(String module, Action action, Menu menu) {
        AccessResourceInfo currentInfo = AccessResourceInfoSession.getInfo();
        String originPath = Optional.ofNullable(currentInfo)
                .map(AccessResourceInfo::getOriginPath)
                .orElse(null);
        AccessResourceInfo info;
        String sessionPath = ResourcePath.generatorPath(action.getModel(), action.getName());
        if (sessionPath.equals(originPath)) {
            info = currentInfo;
            info.setPath(sessionPath);
            info.setIsFixed(false);
        } else {
            info = new AccessResourceInfo(originPath);
        }
        info.setModule(module);
        info.setModel(action.getModel());
        info.setActionName(action.getName());
        if (menu != null) {
            info.setMenu(menu.getName());
        }
        if (action instanceof ViewAction) {
            info.setViewAction((ViewAction) action);
        }
        return info;
    }
}
