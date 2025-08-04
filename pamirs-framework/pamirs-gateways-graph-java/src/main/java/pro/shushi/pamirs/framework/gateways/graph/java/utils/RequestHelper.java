package pro.shushi.pamirs.framework.gateways.graph.java.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pro.shushi.pamirs.framework.common.spi.SessionAfterApi;
import pro.shushi.pamirs.framework.common.spi.SessionPrepareApi;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.HealthCheckController;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.PamirsLic;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsClientRequestParam;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Supplier;

/**
 * 请求帮助类
 * <p>
 * 2021/9/13 4:08 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class RequestHelper {

    private static final HoldKeeper<SessionPrepareApi> sessionPrepareApiHoldKeeper = new HoldKeeper<>();

    private static final HoldKeeper<SessionAfterApi> sessionAfterApiHoldKeeper = new HoldKeeper<>();

    public static SessionPrepareApi getSessionPrepareApi() {
        return sessionPrepareApiHoldKeeper.supply(() -> Spider.getDefaultExtension(SessionPrepareApi.class));
    }

    public static SessionAfterApi getSessionAfterApi() {
        return sessionAfterApiHoldKeeper.supply(() -> Spider.getDefaultExtension(SessionAfterApi.class));
    }

    public static PamirsRequestParam preparePamirsRequestParam(String moduleName, HttpServletRequest request, PamirsClientRequestParam gql) {
        PamirsRequestParam requestParam = new PamirsRequestParam();
        requestParam.setQuery(gql.getQuery());
        Map<String, Object> variables = null != gql.getVariables() ? gql.getVariables() : new HashMap<>();
        requestParam.setVariables(new PamirsRequestVariables().setVariables(variables));
        try {
            getSessionPrepareApi().prepare(request, moduleName, requestParam);
        } catch (Throwable e) {
            PamirsRequestResult result = new PamirsRequestResult();
            result.setErrors(JsonUtils.parseObjectList2MapList(Collections.singletonList(Message.init().setLevel(InformationLevelEnum.ERROR).setCode("Oops!").setMessage(e.getMessage()))));
            requestParam.setResult(result);
        }
        return requestParam;
    }

    public static void afterPamirsRequest(HttpServletRequest request, HttpServletResponse response) {
        getSessionAfterApi().after(request, response);
    }

    public static PamirsRequestResult handleModuleAccess(String moduleName, String resolvedModuleName,
                                                         Supplier<PamirsRequestResult> executeFunction) {
        // 系统模块访问控制

        PamirsRequestResult requestResult = Spider.getLoader(PamirsLic.class).getExtension().check(resolvedModuleName);
        ;
        if (requestResult != null) {
            return requestResult;
        }
        return executeFunction.get();
    }

    public static PamirsRequestResult beforeRequestExecute() {
        if (!HealthCheckController.imok()) {
            PamirsRequestResult result = new PamirsRequestResult();
            result.setData(new HashMap<>());
            log.warn("系统启动中,稍后再试...");
            Map<Object, Object> extMap = new HashMap<>();
            extMap.put("success", true);
            extMap.put(ClientGraphQLError.MESSAGES, Collections.singletonList(Message.init().setMessage("系统启动中,稍后再试...")
                    .setLevel(InformationLevelEnum.WARN).setErrorType(null)));
            return result.setExtensions(extMap);
        }
        enableInheritableRequestAttributes();
        return null;
    }

    /**
     * 启用RequestAttributes子线程可见，支持GQL异步查询
     */
    private static void enableInheritableRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true);
    }

    public static <T> List<T> mergeList(List<T> src, List<T> dest) {
        List<T> result = new ArrayList<>(src.size());
        int i = 0;
        for (T t : src) {
            if (null == t) {
                result.add(dest.get(i));
            } else {
                result.add(t);
            }
            i++;
        }
        return result;
    }

    public static String getHeaderModuleName(HttpServletRequest request) {
        return getHeaderModuleName(request, null);
    }

    public static String getHeaderModuleName(HttpServletRequest request, String defaultModuleName) {
        return Optional.ofNullable(request.getHeader(VariableNameConstants.module))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .orElse(defaultModuleName);
    }

}
