package pro.shushi.pamirs.framework.faas.debug;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.faas.debug.api.MainFunDebugApi;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.ExtPointConstants;
import pro.shushi.pamirs.meta.constant.RequestParamConstants;

import java.util.*;

/**
 * debug调试收集{主函数信息}记录
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/5 1:51 下午
 */
@Component
public class MainFunDebugTrace implements SceneAnalysisDebugTraceApi {

    private static final String MAIN_FUN_DEBUG_SCENE = "主函数信息";

    public static void debug(Function function, FunctionComputer computer, Object result, Object... args) {
        try {
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            //函数异常只加ROOT信息
            String funNamespace = (String) PamirsSession.getRequestInfo(RequestParamConstants.FUN_NAMESPACE);
            String funName = (String) PamirsSession.getRequestInfo(RequestParamConstants.FUN_NAME);
            if ((function.getNamespace().equals(funNamespace) && function.getName().equals(funName))
                    || (function.getName()).equals(funName + ExtPointConstants.OVERRIDE)) {
                Map<String, MainFunDebugApi> mainFunDebugApiMap = BeanDefinitionUtils.getBeansOfType(MainFunDebugApi.class);
                List<MainFunDebugApi> mainFunDebugApis = new ArrayList<>(Objects.requireNonNull(mainFunDebugApiMap).values());
                MainFunDebugTrace mainFunDebugTrace = BeanDefinitionUtils.getBean(MainFunDebugTrace.class);
                for (MainFunDebugApi mainFunDebugApi : mainFunDebugApis) {
                    mainFunDebugTrace.addDebugTrace(() -> mainFunDebugApi.debug(function,computer,result,args));
                }
            }
        } catch (Throwable e) {
            //忽略
        }
    }

    @Override
    public String scene() {
        return MAIN_FUN_DEBUG_SCENE;
    }
}
