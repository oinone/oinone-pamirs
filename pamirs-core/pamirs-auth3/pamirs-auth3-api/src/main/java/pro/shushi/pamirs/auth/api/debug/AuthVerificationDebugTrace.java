package pro.shushi.pamirs.auth.api.debug;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.core.common.entry.HoldSupplier;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 权限验证场景追踪
 *
 * @author Adamancy Zhang at 12:26 on 2024-05-20
 */
@Component
public class AuthVerificationDebugTrace implements SceneAnalysisDebugTraceApi {

    private static final String AUTH_VERIFICATION_DEBUG_SCENE = "权限验证追踪";

    private static final HoldSupplier<AuthVerificationDebugTrace> debugTraceHolder = new HoldSupplier<>(() -> BeanDefinitionUtils.getBean(AuthVerificationDebugTrace.class));

    public static void debugAccessModule(Result<Void> result, String module) {
        debug(() -> String.format("验证模块[%s]: %s", module, result.getSuccess()));
    }

    public static void debugAccessHomepage(Result<Void> result, String module) {
        debug(() -> String.format("验证首页[%s]: %s", module, result.getSuccess()));
    }

    public static void debugAccessMenu(Result<Void> result, String module, String name) {
        debug(() -> String.format("[%s] - 验证菜单[%s]: %s", module, name, result.getSuccess()));
    }

    public static void debugAccessAction(Result<Void> result, String model, String name) {
        debug(() -> String.format("验证动作[%s#%s]: %s", model, name, result.getSuccess()));
    }

    public static void debugAccessAction(Result<Void> result, String path) {
        debug(() -> String.format("验证动作路径[%s]: %s", path, result.getSuccess()));
    }

    public static void debugAccessFunction(Result<Void> result, String namespace, String fun) {
        debug(() -> String.format("验证函数[%s#%s]: %s", namespace, fun, result.getSuccess()));
    }

    public static void debugFieldPermissions(Map<String, Long> result, String model) {
        if (result == null || result.isEmpty()) {
            return;
        }
        boolean isAllFields = Optional.ofNullable(result.get(AuthConstants.ALL_FLAG_STRING))
                .map(FieldAuthorizedValueEnum::readable)
                .orElse(false);
        if (isAllFields) {
            return;
        }
        Set<String> readableFields = new LinkedHashSet<>();
        Set<String> writableFields = new LinkedHashSet<>();
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            String field = entry.getKey();
            Long authorizedValue = entry.getValue();
            if (FieldAuthorizedValueEnum.readable(authorizedValue)) {
                readableFields.add(field);
            }
            if (FieldAuthorizedValueEnum.writable(authorizedValue)) {
                writableFields.add(field);
            }
        }
        debugReadableFields(readableFields, model);
        debugWritableFields(writableFields, model);
    }

    public static void debugReadableData(Result<String> result, String model) {
        debugReadableData(result.getData(), model);
    }

    public static void debugWritableData(Result<String> result, String model) {
        debugWritableData(result.getData(), model);
    }

    public static void debugDeletableData(Result<String> result, String model) {
        debugDeletableData(result.getData(), model);
    }

    public static void debugReadableFields(Result<Set<String>> result, String model) {
        debugReadableFields(result.getData(), model);
    }

    public static void debugWritableFields(Result<Set<String>> result, String model) {
        debugWritableFields(result.getData(), model);
    }

    public static void debugReadableData(String result, String model) {
        debug(() -> String.format("模型读权限[%s]: %s", model, result));
    }

    public static void debugWritableData(String result, String model) {
        debug(() -> String.format("模型写权限[%s]: %s", model, result));
    }

    public static void debugDeletableData(String result, String model) {
        debug(() -> String.format("模型删除权限[%s]: %s", model, result));
    }

    public static void debugReadableFields(Set<String> result, String model) {
        debug(() -> String.format("模型可见字段[%s]: %s", model, Optional.ofNullable(result).map(v -> String.join(", ", v)).orElse(null)));
    }

    public static void debugWritableFields(Set<String> result, String model) {
        debug(() -> String.format("模型可编辑字段[%s]: %s", model, Optional.ofNullable(result).map(v -> String.join(", ", v)).orElse(null)));
    }

    private static void debug(Supplier<Object> traceSupplier) {
        try {
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            debugTraceHolder.get().addDebugTrace(traceSupplier);
        } catch (Throwable ignored) {
            // do nothing.
        }
    }

    @Override
    public String scene() {
        return AUTH_VERIFICATION_DEBUG_SCENE;
    }
}
