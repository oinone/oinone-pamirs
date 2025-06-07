package pro.shushi.pamirs.framework.faas.debug;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * debug调试收集根函数异常记录
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/5 1:51 下午
 */

@Component
public class FunExceptionDebugTrace implements SceneAnalysisDebugTraceApi {

    private static final String FUN_EXCEPTION_DEBUG_SCENE = "函数异常信息";

    public static void debug(Function function, FunctionComputer computer, Object result, Object... args) {
        try {
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }

            //函数异常只加ROOT信息
            if (!BeanDefinitionUtils.getBean(FunExceptionDebugTrace.class).isAdded()) {
                BeanDefinitionUtils.getBean(FunExceptionDebugTrace.class).addDebugTrace(() -> {
                    Map<String, Object> debugInfo = new LinkedHashMap<>();
                    Map<String, String> functionInfo = new LinkedHashMap<>();
                    functionInfo.put("namespace", function.getNamespace());
                    functionInfo.put("fun", function.getFun());
                    functionInfo.put("beanName", function.getBeanName());
                    debugInfo.put("函数基本信息", functionInfo);
                    debugInfo.put("函数执行器", computer.type().name());
                    debugInfo.put("result", JsonUtils.toJSONString(result));
                    debugInfo.put("args", JsonUtils.toJSONString(args));
                    return debugInfo;
                });
            }
        } catch (Throwable e) {
            //忽略
        }
    }

    @Override
    public String scene() {
        return FUN_EXCEPTION_DEBUG_SCENE;
    }
}
