package pro.shushi.pamirs.auth.api.debug;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.behavior.AuthorizedValueComputer;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthL2Cache;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthSharedCache;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionApi;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.manager.AuthQueryAuthorizationOperator;
import pro.shushi.pamirs.auth.api.service.manager.AuthUserRoleOperator;
import pro.shushi.pamirs.auth.api.utils.SessionPathHelper;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestResultDeal;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限上下文
 *
 * @author Adamancy Zhang at 16:43 on 2024-05-17
 */
@Component
public class FrontRequestDeal4AuthSession implements FrontRequestResultDeal, FrontRequestExceptionDeal {

    private static final Pair<String, String> STACKTRACE_AUTH_DIAGNOSIS = ImmutablePair.of("authDiagnosis", "权限诊断");

    private static final Pair<String, String> STACKTRACE_AUTH_SESSION = ImmutablePair.of("authSession", "权限上下文");

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthUserRoleOperator authUserRoleOperator;

    @Autowired
    private AuthQueryAuthorizationOperator authQueryAuthorizationOperator;

    @Override
    public void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput) {
        if (SceneAnalysisDebugTraceApi.debugLevel() == 2) {
            addDebugInfo(executionResult, ClientGraphQLError.build(STACKTRACE_AUTH_SESSION, JsonUtils.toJSONString(generatorSessionMap(),
                    SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.BrowserCompatible)));
        }
    }

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        Map<String, Object> diagnosisMap = generatorDiagnosisMap();
        if (diagnosisMap != null) {
            result.getErrors().add(ClientGraphQLError.build(STACKTRACE_AUTH_DIAGNOSIS, JsonUtils.toJSONString(diagnosisMap,
                    SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.BrowserCompatible)));
        }
        if (SceneAnalysisDebugTraceApi.debugLevel() == 2) {
            result.getErrors().add(ClientGraphQLError.build(STACKTRACE_AUTH_SESSION, JsonUtils.toJSONString(generatorSessionMap(),
                    SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.BrowserCompatible)));
        }
    }

    @Override
    public int priority() {
        return 501;
    }

    private Map<String, Object> generatorSessionMap() {
        Map<String, Object> sessionMap = new LinkedHashMap<>();
        fillResourceInfoSession(sessionMap);
        List<AuthRole> roles = fillRoleSession(sessionMap);
        fillRolePermissionsForCache(sessionMap);
        if (roles != null) {
            fillRolePermissionsForDB(sessionMap, roles);
        }
        return sessionMap;
    }

    private void fillResourceInfoSession(Map<String, Object> sessionMap) {
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        if (info == null) {
            return;
        }
        info = info.clone();
        ModuleDefinition moduleDefinition = info.getModuleDefinition();
        if (moduleDefinition != null) {
            ModuleDefinition cloneModuleDefinition = new ModuleDefinition();
            cloneModuleDefinition.setModule(cloneModuleDefinition.getModule());
            cloneModuleDefinition.setName(cloneModuleDefinition.getName());
            cloneModuleDefinition.setDsKey(cloneModuleDefinition.getDsKey());
            cloneModuleDefinition.setHomePageModel(cloneModuleDefinition.getHomePageModel());
            cloneModuleDefinition.setHomePageName(cloneModuleDefinition.getHomePageName());
            info.setModuleDefinition(cloneModuleDefinition);
        }
        ViewAction viewAction = info.getViewAction();
        if (viewAction != null) {
            ViewAction cloneViewAction = new ViewAction();
            cloneViewAction.setModel(viewAction.getModel());
            cloneViewAction.setName(viewAction.getName());
            cloneViewAction.setActionType(viewAction.getActionType());
            cloneViewAction.setContextType(viewAction.getContextType());
            cloneViewAction.setTarget(viewAction.getTarget());
            cloneViewAction.setLoad(viewAction.getLoad());
            cloneViewAction.setDomain(viewAction.getDomain());
            cloneViewAction.setFilter(viewAction.getFilter());
            cloneViewAction.setMask(viewAction.getMask());
            cloneViewAction.setResViewName(viewAction.getResViewName());
            cloneViewAction.setModule(viewAction.getModule());
            cloneViewAction.setModuleName(viewAction.getModuleName());
            cloneViewAction.setResModule(viewAction.getResModule());
            cloneViewAction.setResModuleName(viewAction.getResModuleName());
            info.setViewAction(cloneViewAction);
        }
        sessionMap.put("sessionPath", info);
    }

    private List<AuthRole> fillRoleSession(Map<String, Object> sessionMap) {
        Set<Long> roleIds = AuthRoleSession.getCurrentRoles();
        if (CollectionUtils.isEmpty(roleIds)) {
            return null;
        }
        sessionMap.put("cacheRoleIds", roleIds);
        List<AuthRole> roles = authUserRoleOperator.fetchActiveRoles(PamirsSession.getUserId());
        sessionMap.put("dbRoles", roles);
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        return roles;
    }

    private void fillRolePermissionsForCache(Map<String, Object> sessionMap) {
        Map<String, Object> cache = AuthL2Cache.getAllCache();
        if (MapUtils.isNotEmpty(cache)) {
            sessionMap.put("memoryCache", JSON.parse(JSON.toJSONString(cache)));
        }
        cache = AuthSharedCache.getAllCache();
        if (MapUtils.isNotEmpty(cache)) {
            sessionMap.put("memorySharedCache", AuthSharedCache.getAllCache());
        }
    }

    private void fillRolePermissionsForDB(Map<String, Object> sessionMap, List<AuthRole> roles) {
        Set<Long> roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());

        Map<Long, List<AuthResourceAuthorization>> resourceAuthorizations = authQueryAuthorizationOperator.queryRoleResourceAuthorizations(roleIds);
        Map<Long, List<AuthModelAuthorization>> modelAuthorizations = authQueryAuthorizationOperator.queryRoleModelAuthorizations(roleIds);
        Map<Long, List<AuthFieldAuthorization>> fieldAuthorizations = authQueryAuthorizationOperator.queryRoleFieldAuthorizations(roleIds);
        Map<Long, List<AuthRowAuthorization>> rowAuthorizations = authQueryAuthorizationOperator.queryRoleRowAuthorizations(roleIds);

        Map<String, Object> simpleRolePermissionMap = new LinkedHashMap<>(4);
        simpleRolePermissionMap.put("resourceAuthorizations", resourceAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(),
                entry -> entry.getValue().stream().collect(Collectors.toMap(AuthResourceAuthorization::getPath, AuthResourceAuthorization::getAuthorizedValue, AuthorizedValueComputer.AUTHORIZE::compute)))));
        simpleRolePermissionMap.put("modelAuthorizations", modelAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(),
                entry -> entry.getValue().stream().collect(Collectors.toMap(AuthModelAuthorization::getModel, AuthModelAuthorization::getAuthorizedValue, AuthorizedValueComputer.AUTHORIZE::compute)))));
        simpleRolePermissionMap.put("fieldAuthorizations", fieldAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(),
                entry -> {
                    Map<String, Map<String, Long>> fieldPermissions = new LinkedHashMap<>();
                    for (AuthFieldAuthorization fieldAuthorization : entry.getValue()) {
                        Long authorizedValue = fieldAuthorization.getAuthorizedValue();
                        fieldPermissions.computeIfAbsent(fieldAuthorization.getModel(), k -> new LinkedHashMap<>())
                                .compute(fieldAuthorization.getField(), (k, v) -> {
                                    if (v == null) {
                                        return authorizedValue;
                                    }
                                    return AuthorizedValueComputer.AUTHORIZE.compute(v, authorizedValue);
                                });
                    }
                    return fieldPermissions;
                })));
        simpleRolePermissionMap.put("rowAuthorizations", rowAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(),
                entry -> {
                    Map<String, Map<String, Long>> rowPermissions = new LinkedHashMap<>();
                    for (AuthRowAuthorization rowAuthorization : entry.getValue()) {
                        String filter = rowAuthorization.getFilter();
                        if (StringUtils.isBlank(filter)) {
                            continue;
                        }
                        Long authorizedValue = rowAuthorization.getAuthorizedValue();
                        rowPermissions.computeIfAbsent(rowAuthorization.getModel(), k -> new LinkedHashMap<>())
                                .compute(filter, (k, v) -> {
                                    if (v == null) {
                                        return authorizedValue;
                                    }
                                    return AuthorizedValueComputer.AUTHORIZE.compute(v, authorizedValue);
                                });
                    }
                    return rowPermissions;
                })));
        sessionMap.put("simpleRoleAuthorizations", simpleRolePermissionMap);

        Map<String, Object> fullRolePermissionMap = new LinkedHashMap<>(4);
        fullRolePermissionMap.put("resourceAuthorizations", resourceAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue)));
        fullRolePermissionMap.put("modelAuthorizations", modelAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue)));
        fullRolePermissionMap.put("fieldAuthorizations", fieldAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue)));
        fullRolePermissionMap.put("rowAuthorizations", rowAuthorizations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue)));
        sessionMap.put("fullRoleAuthorizations", fullRolePermissionMap);
    }

    private Map<String, Object> generatorDiagnosisMap() {
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        if (info == null) {
            return null;
        }
        String namespace = null;
        String fun = null;
        if (info.isFixed()) {
            if (!info.isFunction()) {
                namespace = info.getModel();
                fun = info.getActionName();
            }
        } else {
            ResourcePath lastPath = info.getLastPath();
            if (lastPath != null && ResourcePathMetadataType.ACTION.equals(lastPath.getType())) {
                namespace = lastPath.getModel();
                fun = lastPath.getName();
            }
        }
        AccessPermissionApi accessPermissionApi = AuthApiHolder.getAccessPermissionApi();
        Map<String, Object> diagnosisMap = new LinkedHashMap<>();
        diagnosisMap.put("当前访问动作", info.toString());
        String module = info.getModule();
        if (StringUtils.isNotBlank(module)) {
            diagnosisMap.put("待验证模块", module);
            diagnosisMap.put("模块验证", accessPermissionApi.isAccessModule(module));
            String homepage = info.getMenu();
            if (StringUtils.isNotBlank(homepage)) {
                diagnosisMap.put("首页验证", accessPermissionApi.isAccessHomepage(module));
            }
        }
        String menu = info.getMenu();
        if (StringUtils.isNotBlank(menu)) {
            diagnosisMap.put("待验证菜单", menu);
            diagnosisMap.put("菜单验证", accessPermissionApi.isAccessMenu(module, menu));
        }
        if (StringUtils.isNoneBlank(namespace, fun)) {
            diagnosisMap.put("待验证动作命名空间", namespace);
            diagnosisMap.put("待验证动作名称", fun);
            String path = SessionPathHelper.getActionPath(namespace, fun);
            if (StringUtils.isBlank(path)) {
                diagnosisMap.put("动作验证", AuthApiHolder.getAccessPermissionApi().isAccessAction(namespace, fun));
            } else {
                diagnosisMap.put("待验证动作路径", path);
                diagnosisMap.put("动作验证", AuthApiHolder.getAccessPermissionApi().isAccessAction(path));
            }
        }
        return diagnosisMap;
    }
}
