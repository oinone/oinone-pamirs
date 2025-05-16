package pro.shushi.pamirs.auth.api.runtime.hook;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.fun.VarType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;

import java.util.Map;
import java.util.Optional;

/**
 * 字段权限Hook
 *
 * @author Adamancy Zhang at 16:24 on 2024-01-06
 */
@Base
@Slf4j
@Component
public class FieldPermissionHook implements HookAfter {

    @Hook
    @Override
    public Object run(Function function, Object ret) {
        if (ret == null) {
            return null;
        }
        String model = getModel(function);
        if (StringUtils.isBlank(model)) {
            return ret;
        }
        Map<String, Long> fieldPermissions = getFieldPermissions(model);
        if (MapUtils.isEmpty(fieldPermissions)) {
            return ret;
        }
        if (log.isDebugEnabled()) {
            log.debug("Using field permissions. model: {}, permissions: {}", model, fieldPermissions);
        }
        boolean isAllFields = Optional.ofNullable(fieldPermissions.get(AuthConstants.ALL_FLAG_STRING))
                .map(FieldAuthorizedValueEnum::readable)
                .orElse(false);
        if (isAllFields) {
            return ret;
        }
        Object result;
        if (ret.getClass().isArray()) {
            Object[] obj = (Object[]) ret;
            result = obj[0];
        } else {
            result = ret;
        }
        if (result == null) {
            return ret;
        }
        if (result instanceof Pagination) {
            paginationProcess(model, fieldPermissions, (Pagination<?>) result);
            return ret;
        }
        if (result instanceof Iterable) {
            iterableProcess(model, fieldPermissions, (Iterable<?>) result);
            return ret;
        }
        if (result.getClass().isArray()) {
            arrayProcess(model, fieldPermissions, (Object[]) result);
            return ret;
        }
        Map<String, Object> dMap = getDMap(result);
        if (dMap != null) {
            dMapProcess(model, fieldPermissions, dMap);
            return ret;
        }
        return ret;
    }

    private String getModel(Function function) {
        String model = Optional.ofNullable(function.getReturnType())
                .map(VarType::getModel)
                .orElse(null);
        if (Pagination.MODEL_MODEL.equals(model)) {
            model = function.getNamespace();
        }
        if (StringUtils.isBlank(model)) {
            return null;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            return null;
        }
        return modelConfig.getModel();
    }

    private Map<String, Long> getFieldPermissions(String model) {
        AuthResult<Map<String, Long>> fieldPermissions = AuthApiHolder.getDataPermissionApi().fetchFieldPermissions(model);
        if (fieldPermissions.isFetch()) {
            return fieldPermissions.getData();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDMap(Object result) {
        if (result instanceof D) {
            return ((D) result).get_d();
        }
        if (result instanceof Map) {
            return (Map<String, Object>) result;
        }
        return null;
    }

    private void paginationProcess(String model, Map<String, Long> fieldPermissions, Pagination<?> result) {
        iterableProcess(model, fieldPermissions, result.getContent());
    }

    private void iterableProcess(String model, Map<String, Long> fieldPermissions, Iterable<?> result) {
        for (Object item : result) {
            Map<String, Object> dMap = getDMap(item);
            if (dMap != null) {
                dMapProcess(model, fieldPermissions, dMap);
            }
        }
    }

    private void arrayProcess(String model, Map<String, Long> fieldPermissions, Object[] result) {
        for (Object item : result) {
            Map<String, Object> dMap = getDMap(item);
            if (dMap != null) {
                dMapProcess(model, fieldPermissions, dMap);
            }
        }
    }

    private void dMapProcess(String model, Map<String, Long> fieldPermissions, Map<String, Object> dMap) {
        for (Map.Entry<String, Long> entry : fieldPermissions.entrySet()) {
            String key = entry.getKey();
            Object value = dMap.get(key);
            if (value == null) {
                continue;
            }
            if ((entry.getValue() & FieldAuthorizedValueEnum.READ.value()) == 0) {
                dMap.put(key, null);
            }
        }
    }
}
