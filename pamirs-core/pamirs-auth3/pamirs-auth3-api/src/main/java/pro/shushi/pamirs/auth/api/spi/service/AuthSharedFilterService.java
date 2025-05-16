package pro.shushi.pamirs.auth.api.spi.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthSharedCache;
import pro.shushi.pamirs.auth.api.runtime.session.AuthSharedAuthorizationSession;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionPrepareApi;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessResourceInfoPrepareApi;
import pro.shushi.pamirs.auth.api.spi.AuthFilterService;
import pro.shushi.pamirs.auth.api.utils.AuthVerificationHelper;
import pro.shushi.pamirs.auth.api.utils.SharedResourcePathParser;
import pro.shushi.pamirs.boot.base.model.SharedPage;
import pro.shushi.pamirs.boot.base.model.SharedPageViewAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 分享过滤服务
 *
 * @author Adamancy Zhang at 23:35 on 2024-04-15
 */
@Order(55)
@Component
public class AuthSharedFilterService implements AccessPermissionPrepareApi, AccessResourceInfoPrepareApi, AuthFilterService {

    private static final String SHARED_CODE = "sharedCode";

    private static final String AUTHORIZATION_CODE = "authorizationCode";

    private static final Supplier<Boolean> TRUE_SUPPLIER = () -> true;

    private static final Supplier<Boolean> FALSE_SUPPLIER = () -> false;

    @Autowired
    private SharedResourcePathParser sharedResourcePathParser;

    @Override
    public Boolean isAccessModule(String module) {
        return consumerAuthorizationCode((paths) -> AuthVerificationHelper.isAccessModule(paths, module));
    }

    @Override
    public Boolean isAccessHomepage(String module) {
        return simpleVerifyAuthorizationCode(FALSE_SUPPLIER);
    }

    @Override
    public Boolean isAccessMenu(String module, String name) {
        return simpleVerifyAuthorizationCode(FALSE_SUPPLIER);
    }

    @Override
    public Boolean isAccessFunction(String namespace, String fun) {
        if (StringUtils.isBlank(AuthSharedAuthorizationSession.getSharedCode())) {
            return null;
        }
        if (SharedPage.MODEL_MODEL.equals(namespace) && SharedPage.SHARED_LOAD_FUN.equals(fun)) {
            return true;
        }
        return simpleVerifyAuthorizationCode(TRUE_SUPPLIER);
    }

    @Override
    public Boolean isAccessAction(String model, String name) {
        return consumerAuthorizationCode((paths) -> AuthVerificationHelper.isAccessAction(paths, model, name));
    }

    @Override
    public Boolean isAccessAction(String actionPath) {
        return consumerAuthorizationCode((paths) -> AuthVerificationHelper.isAccessAction(paths, actionPath));
    }

    @Override
    public AuthResult<Map<String, Long>> fetchFieldPermissions(String model) {
        return simpleVerifyAuthorizationCode(AuthResult::success);
    }

    @Override
    public AuthResult<String> fetchModelFilterForRead(String model) {
        return simpleVerifyAuthorizationCode(AuthResult::success);
    }

    @Override
    public AuthResult<String> fetchModelFilterForWrite(String model) {
        return simpleVerifyAuthorizationCode(AuthResult::success);
    }

    @Override
    public AuthResult<String> fetchModelFilterForDelete(String model) {
        return simpleVerifyAuthorizationCode(AuthResult::success);
    }

    @Override
    public void prepareAccessPermission(Function function, Object... args) {
        if (SharedPage.MODEL_MODEL.equals(function.getNamespace()) && SharedPage.SHARED_LOAD_FUN.equals(function.getFun())) {
            prepareSharedCode(function, args);
            return;
        }
        prepareAuthorizationCode(function, args);
    }

    @Override
    public void prepareAccessInfo(Function function, String model, String actionName, FunctionSourceEnum source) {
        if (!FunctionSourceEnum.ACTION.equals(source)) {
            return;
        }
        if (StringUtils.isBlank(AuthSharedAuthorizationSession.getAuthorizationCode())) {
            return;
        }
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        if (info == null || !info.isFixed()) {
            return;
        }
        String originPath = info.getOriginPath();
        AccessResourceInfo newInfo = sharedResourcePathParser.parseAccessInfo(originPath);
        if (newInfo != null) {
            ResourcePath lastPath = newInfo.getLastPath();
            if (lastPath == null) {
                if (!model.equals(newInfo.getModel()) || !actionName.equals(newInfo.getActionName())) {
                    return;
                }
            } else if (!ResourcePathMetadataType.ACTION.equals(lastPath.getType()) || !model.equals(lastPath.getModel()) || !actionName.equals(lastPath.getName())) {
                return;
            }
            AccessResourceInfoSession.setInfo(newInfo);
        }
    }

    protected Boolean consumerAuthorizationCode(PathsConsumer consumer) {
        String authorizationCode = AuthSharedAuthorizationSession.getAuthorizationCode();
        if (StringUtils.isBlank(authorizationCode)) {
            return null;
        }
        Set<String> paths = AuthSharedCache.getPaths(authorizationCode);
        if (CollectionUtils.isEmpty(paths)) {
            return false;
        }
        Boolean isAccess = consumer.apply(paths);
        if (isAccess == null) {
            isAccess = Boolean.FALSE;
        }
        return isAccess;
    }

    protected <T> T simpleVerifyAuthorizationCode(Supplier<T> defaultValueSupplier) {
        if (StringUtils.isBlank(AuthSharedAuthorizationSession.getAuthorizationCode())) {
            return null;
        }
        return defaultValueSupplier.get();
    }

    protected void prepareSharedCode(Function function, Object... args) {
        if (AuthSharedAuthorizationSession.isInit()) {
            return;
        }
        String sharedCode = getSharedCodeByArgs(args);
        if (StringUtils.isBlank(sharedCode)) {
            return;
        }
        AuthSharedAuthorizationSession.setSession(sharedCode, null);
    }

    private void prepareAuthorizationCode(Function function, Object... args) {
        if (AuthSharedAuthorizationSession.isInit()) {
            return;
        }
        String sharedCode = getSharedCode();
        if (StringUtils.isBlank(sharedCode)) {
            return;
        }
        String authorizationCode = AuthApiHolder.getAuthSharedCodeCacheService().get(sharedCode);
        if (StringUtils.isBlank(authorizationCode)) {
            return;
        }
        if (authorizationCode.equals(getAuthorizationCode())) {
            AuthSharedAuthorizationSession.setSession(sharedCode, authorizationCode);
        } else {
            AuthSharedAuthorizationSession.accessDenied();
        }
    }

    protected String getSharedCode() {
        return FetchUtil.fetchVariables(SHARED_CODE);
    }

    protected String getSharedCodeByArgs(Object... args) {
        String sharedCode = null;
        for (Object arg : args) {
            if (arg instanceof SharedPageViewAction) {
                SharedPageViewAction page = (SharedPageViewAction) arg;
                sharedCode = page.getSharedCode();
            }
        }
        return sharedCode;
    }

    protected String getAuthorizationCode() {
        return FetchUtil.fetchVariables(AUTHORIZATION_CODE);
    }

    @FunctionalInterface
    protected interface PathsConsumer {

        Boolean apply(Set<String> paths);
    }
}
