package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthL2Cache;
import pro.shushi.pamirs.auth.api.runtime.spi.VerificationPermissionApi;
import pro.shushi.pamirs.auth.api.utils.AuthFetchPermissionHelper;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 默认验证权限实现
 *
 * @author Adamancy Zhang at 10:46 on 2024-01-29
 */
@Order
@Component
@SPI.Service
public class DefaultVerificationPermission implements VerificationPermissionApi {

    @Override
    public Boolean isAccessModule(Set<String> accessModules, String module) {
        List<String> maybeMatchedPaths = Arrays.asList(
                ResourcePath.PATH_SPLIT + module,
                ResourcePath.PATH_SPLIT + module + AuthConstants.ALL_FLAG_PATH_SUFFIX
        );
        for (String maybeMatedPath : maybeMatchedPaths) {
            if (accessModules.contains(maybeMatedPath)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean isAccessHomepage(Set<String> accessHomepages, String module) {
        List<String> maybeMatchedPaths = Arrays.asList(
                ResourcePath.PATH_SPLIT + module + AuthConstants.HOMEPAGE_PATH_SUFFIX,
                ResourcePath.PATH_SPLIT + module + AuthConstants.ALL_FLAG_PATH_SUFFIX
        );
        for (String maybeMatedPath : maybeMatchedPaths) {
            if (accessHomepages.contains(maybeMatedPath)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean isAccessMenu(Set<String> accessMenus, String module, String name) {
        List<String> maybeMatchedPaths = Arrays.asList(
                ResourcePath.PATH_SPLIT + module + ResourcePath.PATH_SPLIT + name,
                ResourcePath.PATH_SPLIT + module + AuthConstants.ALL_FLAG_PATH_SUFFIX
        );
        for (String maybeMatedPath : maybeMatchedPaths) {
            if (accessMenus.contains(maybeMatedPath)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean isAccessFunction(String namespace, String fun) {
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        if (info.getViewAction() == null) {
            return Boolean.FALSE;
        }
        String module = info.getModule();
        if (StringUtils.isBlank(module) && !info.isActionPath()) {
            return Boolean.FALSE;
        }
        String homepage = info.getHomepage();
        if (info.getLastPath() == null && StringUtils.isNotBlank(homepage)) {
            AuthResult<Set<String>> accessHomepageResult = AuthFetchPermissionHelper.fetchHomepagePermissions();
            Set<String> accessHomepages = accessHomepageResult.getData();
            if (accessHomepages == null) {
                return accessHomepageResult.isFetch();
            }
            return isAccessHomepage(accessHomepages, info.getModule());
        }
        String menu = info.getMenu();
        if (info.getLastPath() == null && StringUtils.isNotBlank(menu)) {
            AuthResult<Set<String>> accessMenuResult = AuthFetchPermissionHelper.fetchMenuPermissions(module);
            Set<String> accessMenus = accessMenuResult.getData();
            if (accessMenus == null) {
                return accessMenuResult.isFetch();
            }
            return isAccessMenu(accessMenus, module, menu);
        }
        AuthResult<Set<String>> result = AuthFetchPermissionHelper.fetchActionPermissions();
        Set<String> accessActions = result.getData();
        if (accessActions == null) {
            return result.isFetch();
        }
        return isAccessAction(accessActions, info.toString());
    }

    @Override
    public Boolean isAccessAction(Set<String> accessActions, String model, String name) {
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        List<String> maybeMatchedPaths;
        String sessionPath = info.toString();
        boolean isAllMatch = true;
        if (info.isFixed()) {
            isAllMatch = false;
            maybeMatchedPaths = Collections.singletonList(info.getPath());
        } else {
            sessionPath = sessionPath + ResourcePath.PATH_SPLIT + info.generatorActionPath(model, name).toString();
            maybeMatchedPaths = Arrays.asList(
                    sessionPath,
                    ResourcePath.PATH_SPLIT + new ResourcePath(ResourcePathMetadataType.ACTION, model, name).toString(),
                    ResourcePath.generatorPath(model, name)
            );
        }
        if (isAllMatch) {
            for (String accessAction : accessActions) {
                if (maybeMatchedPaths.contains(accessAction)) {
                    return Boolean.TRUE;
                }
                if (isAllFlagMatch(accessAction, sessionPath)) {
                    Set<String> pathMappings = AuthL2Cache.getPathMappings(accessAction);
                    if (pathMappings.contains(sessionPath)) {
                        return Boolean.TRUE;
                    }
                }
            }
        } else {
            for (String maybeMatedPath : maybeMatchedPaths) {
                if (accessActions.contains(maybeMatedPath)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean isAccessAction(Set<String> accessActions, String path) {
        if (accessActions.contains(path)) {
            return Boolean.TRUE;
        }
        for (String accessAction : accessActions) {
            if (isAllFlagMatch(accessAction, path)) {
                Set<String> pathMappings = AuthL2Cache.getPathMappings(accessAction);
                if (pathMappings.contains(path)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean isManagementModule(Set<String> managementModules, String module) {
        return isAccessModule(managementModules, module);
    }

    @Override
    public Boolean isManagementHomepage(Set<String> managementHomepageModules, String module) {
        return isAccessHomepage(managementHomepageModules, module);
    }

    @Override
    public Boolean isManagementMenu(Set<String> managementMenus, String module, String name) {
        return isAccessMenu(managementMenus, module, name);
    }

    @Override
    public Boolean isManagementAction(Set<String> accessActions, String model, String name) {
        return isAccessAction(accessActions, model, name);
    }

    @Override
    public Boolean isManagementAction(Set<String> accessActions, String path) {
        return isAccessAction(accessActions, path);
    }

    private Boolean isAllFlagMatch(String match, String path) {
        if (match.endsWith(AuthConstants.ALL_FLAG_PATH_SUFFIX)) {
            int pathLength = path.length();
            int maxLength = match.length() - AuthConstants.ALL_FLAG_PATH_SUFFIX.length();
            String matchPath = match.substring(0, maxLength);
            if (pathLength >= maxLength) {
                if (pathLength == maxLength) {
                    if (matchPath.equals(path)) {
                        return Boolean.TRUE;
                    }
                } else {
                    if (path.startsWith(matchPath)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }
}
