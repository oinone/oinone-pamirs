package pro.shushi.pamirs.framework.faas.debug.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * debug调试收集{主函数信息}记录Api的默认实现
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/9/4 1:51 下午
 */
@Component
public class DefaultMainFunDebugApi implements MainFunDebugApi {
    @Override
    public Map<String, Object> debug(Function function, FunctionComputer computer, Object result, Object... args) {
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
    }
}
