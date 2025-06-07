package pro.shushi.pamirs.auth.api.loader.entity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.cache.AccessPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.cache.ManagementPermissionCacheApi;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthVerificationHelper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.*;

/**
 * 权限加载上下文
 *
 * @author Adamancy Zhang at 16:16 on 2024-09-12
 */
public class PermissionLoadContext {

    /**
     * 权限访问验证服务
     */
    private final AuthAccessService authAccessService;

    /**
     * 当前加载角色ID集合
     */
    private final Set<Long> roleIds;

    /**
     * 可分配元数据集合
     */
    private final AllotMetadataCollection allotMetadataCollection;

    /**
     * 扩展加载元数据集合
     */
    private final ExtendLoadMetadataCollection extendLoadMetadataCollection;

    /**
     * 可访问模块
     */
    private final Set<String> accessModules;

    /**
     * 可访问首页
     */
    private final Set<String> accessHomepages;

    /**
     * 可访问模块菜单
     */
    private final Map<String, Set<String>> accessModuleMenus;

    /**
     * 可访问动作
     */
    private final Set<String> accessActions;

    /**
     * 可管理模块
     */
    private final Set<String> managementModules;

    /**
     * 可管理首页
     */
    private final Set<String> managementHomepages;

    /**
     * 可管理模块菜单
     */
    private final Map<String, Set<String>> managementModuleMenus;

    /**
     * 可管理动作
     */
    private final Set<String> managementActions;

    /**
     * 当前已加载的全部节点
     */
    private List<PermissionNode> currentLoadNodes;

    protected PermissionLoadContext(AuthAccessService authAccessService,
                                    Set<Long> roleIds,
                                    AllotMetadataCollection allotMetadataCollection,
                                    Set<String> accessModules,
                                    Set<String> accessHomepages,
                                    Map<String, Set<String>> accessModuleMenus,
                                    Set<String> accessActions,
                                    Set<String> managementModules,
                                    Set<String> managementHomepages,
                                    Map<String, Set<String>> managementModuleMenus,
                                    Set<String> managementActions) {
        this.authAccessService = authAccessService;
        this.roleIds = roleIds;
        this.allotMetadataCollection = allotMetadataCollection;
        this.extendLoadMetadataCollection = new ExtendLoadMetadataCollection(generatorLoadActionPermissions());
        this.accessModules = accessModules;
        this.accessHomepages = accessHomepages;
        this.accessModuleMenus = accessModuleMenus;
        this.accessActions = accessActions;
        this.managementModules = managementModules;
        this.managementHomepages = managementHomepages;
        this.managementModuleMenus = managementModuleMenus;
        this.managementActions = managementActions;
    }

    public static PermissionLoadContext generatorContext(AuthAccessService authAccessService, AllotMetadataCollection allotMetadataCollection) {
        return new PermissionLoadContext(
                authAccessService,
                Collections.emptySet(),
                allotMetadataCollection,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptyMap(),
                new HashSet<>(),
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptyMap(),
                Collections.emptySet()
        );
    }

    public static PermissionLoadContext generatorContext(AuthAccessService authAccessService,
                                                         AllotMetadataCollection allotMetadataCollection,
                                                         Set<Long> roleIds) {
        Set<String> allotModules = allotMetadataCollection.getAllotModuleModules();
        List<String> actionModels = allotMetadataCollection.getActionModels();
        List<String> actionNames = allotMetadataCollection.getActionNames();

        Set<String> accessModules;
        Set<String> accessHomepages;
        Map<String, Set<String>> accessModuleMenus;
        Set<String> accessActions;
        if (CollectionUtils.isEmpty(roleIds)) {
            accessModules = Collections.emptySet();
            accessHomepages = Collections.emptySet();
            accessModuleMenus = Collections.emptyMap();
            accessActions = new HashSet<>();
        } else {
            AccessPermissionCacheApi accessPermissionCacheApi = AuthApiHolder.getAccessPermissionCacheApi();
            accessModules = accessPermissionCacheApi.fetchAccessModules(roleIds).getData();
            accessHomepages = accessPermissionCacheApi.fetchAccessHomepages(roleIds).getData();
            accessModuleMenus = accessPermissionCacheApi.fetchAccessMenus(roleIds, allotModules).getData();
            if (actionModels.isEmpty()) {
                accessActions = new HashSet<>();
            } else {
                accessActions = accessPermissionCacheApi.fetchAccessActions(roleIds, actionModels, actionNames).getData();
                accessActions.addAll(accessPermissionCacheApi.fetchAccessActions(roleIds, new HashSet<>(actionModels)).getData());
            }
        }

        Set<String> managementModules;
        Set<String> managementHomepages;
        Map<String, Set<String>> managementModuleMenus;
        Set<String> managementActions;
        if (CollectionUtils.isEmpty(roleIds)) {
            managementModules = Collections.emptySet();
            managementHomepages = Collections.emptySet();
            managementModuleMenus = Collections.emptyMap();
            managementActions = Collections.emptySet();
        } else {
            ManagementPermissionCacheApi managementPermissionCacheApi = AuthApiHolder.getManagementPermissionCacheApi();
            managementModules = managementPermissionCacheApi.fetchManagementModules(roleIds).getData();
            managementHomepages = managementPermissionCacheApi.fetchManagementHomepages(roleIds).getData();
            managementModuleMenus = managementPermissionCacheApi.fetchManagementMenus(roleIds, allotModules).getData();
            if (actionModels.isEmpty()) {
                managementActions = new HashSet<>();
            } else {
                managementActions = managementPermissionCacheApi.fetchManagementActions(roleIds, actionModels, actionNames).getData();
                managementActions.addAll(managementPermissionCacheApi.fetchManagementActions(roleIds, new HashSet<>(actionModels)).getData());
            }
        }

        return new PermissionLoadContext(
                authAccessService,
                roleIds,
                allotMetadataCollection,
                accessModules,
                accessHomepages,
                accessModuleMenus,
                accessActions,
                managementModules,
                managementHomepages,
                managementModuleMenus,
                managementActions
        );
    }

    private Set<String> generatorLoadActionPermissions() {
        Iterator<String> modelIterator = allotMetadataCollection.getActionModels().iterator();
        Iterator<String> nameIterator = allotMetadataCollection.getActionNames().iterator();
        Set<String> loadActionPermissions = new HashSet<>();
        while (modelIterator.hasNext() && nameIterator.hasNext()) {
            String model = modelIterator.next();
            String name = nameIterator.next();
            loadActionPermissions.add(model + CharacterConstants.SEPARATOR_OCTOTHORPE + name);
        }
        return loadActionPermissions;
    }

    public AuthAccessService getAuthAccessService() {
        return authAccessService;
    }

    public AllotMetadataCollection getAllotMetadataCollection() {
        return allotMetadataCollection;
    }

    public Boolean isAccessModule(String module) {
        return AuthVerificationHelper.isAccessModule(accessModules, module);
    }

    public Boolean isManagementModule(String module) {
        return AuthVerificationHelper.isManagementModule(managementModules, module);
    }

    public Boolean isAllotModule(String module) {
        return authAccessService.canManagementModule(module).getSuccess();
    }

    public Boolean isAccessHomepage(String module) {
        return AuthVerificationHelper.isAccessHomepage(accessHomepages, module);
    }

    public Boolean isManagementHomepage(String module) {
        return AuthVerificationHelper.isManagementHomepage(managementHomepages, module);
    }

    public Boolean isAllotHomepage(String module) {
        return isAllotModule(module) || authAccessService.canManagementHomepage(module).getSuccess();
    }

    public Boolean isAccessMenu(String module, String name) {
        return AuthVerificationHelper.isAccessMenu(accessModuleMenus.getOrDefault(module, Collections.emptySet()), module, name);
    }

    public Boolean isManagementMenu(String module, String name) {
        return AuthVerificationHelper.isManagementMenu(managementModuleMenus.getOrDefault(module, Collections.emptySet()), module, name);
    }

    public Boolean isAllotMenu(String module, String name) {
        return isAllotModule(module) || authAccessService.canManagementMenu(module, name).getSuccess();
    }

    public Boolean isAccessAction(String path) {
        return AuthVerificationHelper.isAccessAction(accessActions, path);
    }

    public Boolean isManagementAction(String path) {
        return AuthVerificationHelper.isManagementAction(managementActions, path);
    }

    public Boolean isAllotAction(String module, String path) {
        if (StringUtils.isNotBlank(module)) {
            if (isAllotModule(module)) {
                return true;
            }
        }
        return AuthVerificationHelper.isManagementAction(managementActions, path) || authAccessService.canManagementAction(path).getSuccess();
    }

    public Boolean isAllotAction(String module, String menuName, String path) {
        if (StringUtils.isNotBlank(module)) {
            if (isAllotModule(module)) {
                return true;
            }
            if (StringUtils.isNotBlank(menuName)) {
                if (isAllotMenu(module, menuName)) {
                    return true;
                }
            }
        }
        return AuthVerificationHelper.isManagementAction(managementActions, path) || authAccessService.canManagementAction(path).getSuccess();
    }

    public List<PermissionNode> getCurrentLoadNodes() {
        return currentLoadNodes;
    }

    public void setCurrentLoadNodes(List<PermissionNode> currentLoadNodes) {
        this.currentLoadNodes = currentLoadNodes;
    }

    public void addExtendAction(String model, String actionName) {
        this.extendLoadMetadataCollection.addAction(model, actionName);
    }

    public void loadExtendPermissions() {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<String> actionModels = this.extendLoadMetadataCollection.getActionModels();
        List<String> actionNames = this.extendLoadMetadataCollection.getActionNames();
        if (actionModels.isEmpty()) {
            return;
        }
        AccessPermissionCacheApi accessPermissionCacheApi = AuthApiHolder.getAccessPermissionCacheApi();
        accessActions.addAll(accessPermissionCacheApi.fetchAccessActions(roleIds, actionModels, actionNames).getData());
        accessActions.addAll(accessPermissionCacheApi.fetchAccessActions(roleIds, new HashSet<>(actionModels)).getData());
        ManagementPermissionCacheApi managementPermissionCacheApi = AuthApiHolder.getManagementPermissionCacheApi();
        managementActions.addAll(managementPermissionCacheApi.fetchManagementActions(roleIds, actionModels, actionNames).getData());
        managementActions.addAll(managementPermissionCacheApi.fetchManagementActions(roleIds, new HashSet<>(actionModels)).getData());
        this.extendLoadMetadataCollection.clear();
    }

    public void addAccessActions(Set<String> accessActions) {
        this.accessActions.addAll(accessActions);
    }
}
