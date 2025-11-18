package pro.shushi.pamirs.auth.api.runtime.executor;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.fun.VarType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * @author Adamancy Zhang at 21:56 on 2025-11-12
 */
@Slf4j
public class FieldPermissionExecutor {

    public static Object run(Function function, Object ret) {
        if (ret == null) {
            return null;
        }
        String model = getModel(function);
        if (StringUtils.isBlank(model)) {
            return ret;
        }
        filter(model, ret);
        return ret;
    }

    public static void filter(String model, Object ret) {
        Map<String, Long> fieldPermissions = getFieldPermissions(model);
        if (MapUtils.isEmpty(fieldPermissions)) {
            return;
        }
        fieldPermissions = new HashMap<>(fieldPermissions);
        if (log.isDebugEnabled()) {
            log.debug("Using field permissions. model: {}, permissions: {}", model, fieldPermissions);
        }
        boolean isAllFields = isAllFieldReadable(fieldPermissions);
        if (isAllFields || fieldPermissions.isEmpty()) {
            return;
        }
        Object result;
        if (ret.getClass().isArray()) {
            Object[] obj = (Object[]) ret;
            result = obj[0];
        } else {
            result = ret;
        }
        if (result == null) {
            return;
        }
        if (result instanceof Pagination) {
            paginationProcess(model, fieldPermissions, (Pagination<?>) result);
            return;
        }
        if (result instanceof Iterable) {
            iterableProcess(model, fieldPermissions, (Iterable<?>) result);
            return;
        }
        if (result.getClass().isArray()) {
            arrayProcess(model, fieldPermissions, (Object[]) result);
            return;
        }
        Map<String, Object> dMap = getDMap(result);
        if (dMap != null) {
            dMapProcess(model, fieldPermissions, dMap);
        }
    }

    private static boolean isAllFieldReadable(Map<String, Long> fieldPermissions) {
        boolean isAllFlag = Optional.ofNullable(fieldPermissions.get(AuthConstants.ALL_FLAG_STRING))
                .map(FieldAuthorizedValueEnum::readable)
                .orElse(false);
        if (isAllFlag) {
            return true;
        }
        Iterator<Map.Entry<String, Long>> iterator = fieldPermissions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> fieldPermission = iterator.next();
            String field = fieldPermission.getKey();
            if (AuthConstants.ALL_FLAG_STRING.equals(field)) {
                iterator.remove();
                continue;
            }
            if ((fieldPermission.getValue() & FieldAuthorizedValueEnum.READ.value()) == 0) {
                continue;
            }
            iterator.remove();
        }
        return false;
    }

    private static String getModel(Function function) {
        String model = Optional.ofNullable(function.getReturnType())
                .map(VarType::getModel)
                .orElse(null);
        if (Pagination.MODEL_MODEL.equals(model)) {
            model = function.getNamespace();
        }
        if (StringUtils.isBlank(model)) {
            return null;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (modelConfig == null) {
            return null;
        }
        return modelConfig.getModel();
    }

    private static Map<String, Long> getFieldPermissions(String model) {
        AuthResult<Map<String, Long>> fieldPermissions = AuthApiHolder.getDataPermissionApi().fetchFieldPermissions(model);
        if (fieldPermissions.isFetch()) {
            return fieldPermissions.getData();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getDMap(Object result) {
        if (result instanceof D) {
            return ((D) result).get_d();
        }
        if (result instanceof Map) {
            return (Map<String, Object>) result;
        }
        return null;
    }

    private static void paginationProcess(String model, Map<String, Long> fieldPermissions, Pagination<?> result) {
        iterableProcess(model, fieldPermissions, result.getContent());
    }

    private static void iterableProcess(String model, Map<String, Long> fieldPermissions, Iterable<?> result) {
        for (Object item : result) {
            Map<String, Object> dMap = getDMap(item);
            if (dMap != null) {
                dMapProcess(model, fieldPermissions, dMap);
            }
        }
    }

    private static void arrayProcess(String model, Map<String, Long> fieldPermissions, Object[] result) {
        for (Object item : result) {
            Map<String, Object> dMap = getDMap(item);
            if (dMap != null) {
                dMapProcess(model, fieldPermissions, dMap);
            }
        }
    }

    private static void dMapProcess(String model, Map<String, Long> fieldPermissions, Map<String, Object> dMap) {
        for (Map.Entry<String, Long> entry : fieldPermissions.entrySet()) {
            String key = entry.getKey();
            Object value = dMap.get(key);
            if (value == null) {
                continue;
            }
            dMap.put(key, null);
        }
    }
}
