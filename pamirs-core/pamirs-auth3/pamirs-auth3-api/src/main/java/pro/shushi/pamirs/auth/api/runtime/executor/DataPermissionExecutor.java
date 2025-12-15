package pro.shushi.pamirs.auth.api.runtime.executor;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.helper.AuthorizedValueHelper;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionApi;
import pro.shushi.pamirs.auth.api.runtime.spi.DataPermissionApi;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.spi.holder.UserIdentityHolder;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.RSqlConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.Optional;

/**
 * 数据权限执行器
 *
 * @author Adamancy Zhang at 12:32 on 2024-03-08
 */
@Slf4j
public class DataPermissionExecutor {

    private DataPermissionExecutor() {
        // reject create object
    }

    public static Object run(Function function, Object... args) {
        prepareArguments(function, args);
        return Fun.run(function, args);
    }

    public static void prepareArguments(Function function, Object... args) {
        DataPermissionApi dataPermissionApi = AuthApiHolder.getDataPermissionApi();
        String namespace = function.getNamespace();
        String fun = function.getFun();
        List<FunctionTypeEnum> functionTypes = function.getType();

        if (!isAccessModel(dataPermissionApi, namespace, functionTypes)) {
            if (log.isWarnEnabled()) {
                log.warn("Model access denied. namespace: {}, fun: {}, type: {}",
                        function.getNamespace(),
                        function.getFun(),
                        Optional.ofNullable(function.getType()).map(AuthorizedValueHelper::getModelAuthorizedValue).orElse(-1L));
            }
            throw PamirsException.construct(AuthExpEnumerate.AUTH_NO_PERMISSION).errThrow();
        }
        appendFilterToWrapper(dataPermissionApi, namespace, fun, functionTypes, args);
    }

    public static String getFilter(String namespace, String fun) {
        if (isAppendFilter(namespace, fun)) {
            Function function = PamirsSession.getContext().getFunctionAllowNull(namespace, fun);
            if (function == null) {
                log.error("Invalid function. namespace: {}, fun: {}", namespace, fun);
                return null;
            }
            return generatorFilter(namespace, fun, function.getType());
        }
        return null;
    }

    public static String getFilter(String namespace, String fun, List<FunctionTypeEnum> functionTypes) {
        if (isAppendFilter(namespace, fun)) {
            return generatorFilter(namespace, fun, functionTypes);
        }
        return null;
    }

    private static void appendFilterToWrapper(DataPermissionApi dataPermissionApi, String namespace, String fun, List<FunctionTypeEnum> functionTypes, Object... args) {
        IWrapper<?> wrapper = findWrapper(args);
        if (wrapper == null) {
            return;
        }
        if (Models.modelDirective().isReentry(wrapper)) {
            return;
        }
        if (isAppendFilter(namespace, fun)) {
            String filter = generatorFilter(dataPermissionApi, namespace, functionTypes);
            if (StringUtils.isNotBlank(filter)) {
                if (log.isDebugEnabled()) {
                    log.debug("Using filter. namespace: {}, fun: {}, filter: {}", namespace, fun, filter);
                }
                appendFilterToWrapper(wrapper, filter);
            }
        }
        Models.modelDirective().enableReentry(wrapper);
    }

    private static Boolean isAccessModel(DataPermissionApi dataPermissionApi, String namespace, List<FunctionTypeEnum> functionTypes) {
        return checkModelResultProcess(dataPermissionApi.isAccessModel(namespace, functionTypes));
    }

    private static Boolean checkModelResultProcess(AuthResult<Boolean> result) {
        if (result == null) {
            return Boolean.TRUE;
        }
        if (result.isFetch()) {
            Boolean isValid = result.getData();
            if (isValid == null) {
                return Boolean.TRUE;
            }
            return isValid;
        }
        return Boolean.TRUE;
    }

    private static IWrapper<?> findWrapper(Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return null;
        }
        for (Object argument : arguments) {
            if (argument instanceof IWrapper) {
                return (IWrapper<?>) argument;
            }
        }
        return null;
    }

    private static boolean isAppendFilter(String namespace, String fun) {
        AccessPermissionApi accessPermissionApi = AuthApiHolder.getAccessPermissionApi();
        return !accessPermissionApi.isFilterFunction(namespace, fun) &&
                !accessPermissionApi.isFilterFunctionOnlyLogin(namespace, fun);
    }

    private static String generatorFilter(String namespace, String fun, List<FunctionTypeEnum> functionTypes) {
        String filter = generatorFilter(AuthApiHolder.getDataPermissionApi(), namespace, functionTypes);
        if (StringUtils.isNotBlank(filter)) {
            if (log.isDebugEnabled()) {
                log.debug("Using filter. namespace: {}, fun: {}, filter: {}", namespace, fun, filter);
            }
            return filter;
        }
        return null;
    }

    private static String generatorFilter(DataPermissionApi dataPermissionApi, String namespace, List<FunctionTypeEnum> functionTypes) {
        if (UserIdentityHolder.isAdmin()) {
            return getSceneFilter(namespace);
        }
        String dataFilter = getDataFilter(dataPermissionApi, namespace, functionTypes);
        String sceneFilter = getSceneFilter(namespace);
        return connectRSQLByAnd(sceneFilter, dataFilter);
    }

    private static String getSceneFilter(String model) {
        String scene = FetchUtil.fetchScene();
        if (StringUtils.isBlank(scene)) {
            return null;
        }
        ViewAction viewAction = null;
        Action action = CommonApiFactory.getApi(MetaCacheManager.class).fetchAction(model, scene);
        if (action instanceof ViewAction) {
            viewAction = (ViewAction) action;
        }
        if (viewAction == null) {
            return null;
        }
        return viewAction.getFilter();
    }

    private static String getDataFilter(DataPermissionApi dataPermissionApi, String model, List<FunctionTypeEnum> functionTypes) {
        AuthResult<String> result;
        if (FunctionTypeEnum.QUERY.in(functionTypes)) {
            result = dataPermissionApi.fetchModelFilterForRead(model);
        } else if (FunctionTypeEnum.UPDATE.in(functionTypes)) {
            result = dataPermissionApi.fetchModelFilterForWrite(model);
        } else if (FunctionTypeEnum.DELETE.in(functionTypes)) {
            result = dataPermissionApi.fetchModelFilterForDelete(model);
        } else {
            result = null;
        }
        if (result == null || !result.isFetch()) {
            return null;
        }
        return result.getData();
    }

    private static void appendFilterToWrapper(IWrapper<?> wrapper, String filter) {
        wrapper.setRsql(connectRSQLByAnd(wrapper.getRsql(), filter));
    }

    private static String connectRSQLByAnd(String rsql1, String rsql2) {
        if (StringUtils.isBlank(rsql1)) {
            return rsql2;
        }
        if (StringUtils.isBlank(rsql2)) {
            return rsql1;
        }
        return CharacterConstants.LEFT_BRACKET + rsql1 + CharacterConstants.RIGHT_BRACKET +
                RSqlConstants.SPACE_AND +
                CharacterConstants.LEFT_BRACKET + rsql2 + CharacterConstants.RIGHT_BRACKET;
    }
}
