package pro.shushi.pamirs.framework.gateways.graph.java;

import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.async.DeferredResult;
import pro.shushi.pamirs.framework.gateways.graph.java.pool.RequestThreadPool;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.RequestHelper;
import pro.shushi.pamirs.framework.gateways.graph.longpolling.PamirsLongPolling;
import pro.shushi.pamirs.framework.gateways.graph.spi.TranslateErrorService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.protocol.RequestExecutor;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsClientRequestParam;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.JsonUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 请求控制类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
@RestController
@Slf4j
public class RequestController implements ApplicationListener<ApplicationReadyEvent> {

    public volatile static boolean contextIsRunning;

    private static final String REQUEST_ATTRIBUTES_ATTRIBUTE = RequestController.class.getName() + ".REQUEST_ATTRIBUTES";

    @Resource
    private RequestExecutor requestExecutor;

    @Autowired
    private RequestThreadPool requestThreadPool;

    @RequestMapping(
            value = "/pamirs/{moduleName:^[a-zA-Z][a-zA-Z0-9_]+[a-zA-Z0-9]$}",
            method = RequestMethod.GET
    )
    public DeferredResult<String> pamirsGet(@PathVariable("moduleName") String moduleName,
                                            @RequestParam("query") String gql,
                                            @RequestParam(value = "variables", required = false) String variables,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        PamirsClientRequestParam gqlRequest = new PamirsClientRequestParam();
        gqlRequest.setQuery(gql);
        if (!StringUtils.isBlank(variables)) {
            gqlRequest.setVariables(JsonUtils.parseMap(variables));
        }
        return pamirsPost(moduleName, gqlRequest, request, response);
    }

    @SuppressWarnings("unused")
    @RequestMapping(
            value = "/pamirs/{moduleName:^[a-zA-Z][a-zA-Z0-9_]+[a-zA-Z0-9]$}",
            method = RequestMethod.POST
    )
    public DeferredResult<String> pamirsPost(@PathVariable("moduleName") String moduleName,
                                             @RequestBody PamirsClientRequestParam gql,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {

        DeferredResult<String> deferredResult = new DeferredResult<>();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, requestAttributes);
        LocaleContextHolder.setLocale(request.getLocale());

        requestThreadPool.submit(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes, true);
            String resolvedModuleName = resolveModuleName(moduleName, request);
            try {
                // 启动前置检验,系统启动成功才处理请求
                PamirsRequestResult beforeRequestExecute = RequestHelper.beforeRequestExecute();
                if (beforeRequestExecute != null) {
                    deferredResult.setResult(JsonUtils.toJSONString(beforeRequestExecute));
                    return;
                }

                clear();
                PamirsRequestParam requestParam = RequestHelper.preparePamirsRequestParam(moduleName, request, gql);
                PamirsRequestResult result = requestParam.getResult();
                if (result == null) {
                    result = RequestHelper.handleModuleAccess(moduleName, resolvedModuleName, () -> requestExecutor.execute(requestParam));
                }
                TranslateErrorService translateErrorService = Spider.getDefaultExtension(TranslateErrorService.class);
                translateErrorService.translateError(result);
                deferredResult.setResult(JsonUtils.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat));
            } finally {
                RequestHelper.afterPamirsRequest(request, response);
                RequestContextHolder.resetRequestAttributes();
                clear();
            }
        }, deferredResult);

        return deferredResult;
    }

    @SuppressWarnings("unused")
    @RequestMapping(
            value = "/pamirs/{moduleName:^[a-zA-Z][a-zA-Z0-9_]+[a-zA-Z0-9]$}/batch",
            method = RequestMethod.POST
    )
    public String pamirsBatch(@PathVariable("moduleName") String moduleName,
                              @RequestBody List<PamirsClientRequestParam> gqls,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        String resolvedModuleName = resolveModuleName(moduleName, request);
        try {
            // 启动前置检验,系统启动成功才处理请求
            PamirsRequestResult beforeRequestExecute = RequestHelper.beforeRequestExecute();
            if (beforeRequestExecute != null) {
                return JsonUtils.toJSONString(beforeRequestExecute);
            }
            clear();
            List<PamirsRequestResult> result = new ArrayList<>(gqls.size());
            List<PamirsRequestParam> pamirsRequestParamList = new ArrayList<>(gqls.size());
            TranslateErrorService translateErrorService = Spider.getDefaultExtension(TranslateErrorService.class);
            PamirsRequestResult accessResult = RequestHelper.handleModuleAccess(moduleName, resolvedModuleName, () -> null);
            for (PamirsClientRequestParam gql : gqls) {
                if (null != accessResult) {
                    translateErrorService.translateError(accessResult);
                    result.add(accessResult);
                    pamirsRequestParamList.add(null);
                } else {
                    result.add(null);
                    pamirsRequestParamList.add(RequestHelper.preparePamirsRequestParam(resolvedModuleName, request, gql));
                }
            }
            List<PamirsRequestResult> executeResult = requestExecutor.executeAsync(pamirsRequestParamList);
            for (PamirsRequestResult requestResult : executeResult) {
                translateErrorService.translateError(requestResult);
            }
            return JsonUtils.toJSONString(RequestHelper.mergeList(result, executeResult), SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
        } finally {
            RequestHelper.afterPamirsRequest(request, response);
            clear();
        }
    }

    @GetMapping(
            value = "/pamirs/{moduleName:^[a-zA-Z][a-zA-Z0-9_]+[a-zA-Z0-9]$}/longpolling"
    )
    public DeferredResult<String> pamirsLongPollingGet(@PathVariable("moduleName") String moduleName,
                                                       @RequestBody PamirsRequestParam requestParam,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) throws IOException {
        return pamirsLongPollingPost(moduleName, requestParam, request, response);
    }

    @PostMapping(
            value = "/pamirs/{moduleName:^[a-zA-Z][a-zA-Z0-9_]+[a-zA-Z0-9]$}/longpolling"
    )
    public DeferredResult<String> pamirsLongPollingPost(@PathVariable("moduleName") String moduleName,
                                                        @RequestBody PamirsRequestParam requestParam,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws IOException {
        clear();
        RequestHelper.getSessionPrepareApi().prepare(request, moduleName, requestParam);
        return lifeLongPolling(moduleName, requestParam);
    }

    private DeferredResult<String> lifeLongPolling(String moduleName, PamirsRequestParam requestParam) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!contextIsRunning) {
            return null;
        }
        PamirsLongPolling longPolling = new PamirsLongPolling(requestParam.getQuery()) {
            @Override
            protected String execute() {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return lifeLongPollingExecute(moduleName, requestParam);
            }
        };

        clear();
        return longPolling.getDeferredResult();
    }

    private String lifeLongPollingExecute(String moduleName, PamirsRequestParam requestParam) {
        if (PamirsSession.getUserId() == null) {
            PamirsRequestResult result = new PamirsRequestResult();
            Map<String, Object> map = new HashMap<>();
            result.setData(map);
            result.setErrors(new ArrayList<>(0));
            return JsonUtils.toJSONString(result);
        }
        PamirsRequestResult result = RequestHelper
                .handleModuleAccess(moduleName, moduleName, () -> requestExecutor.execute(requestParam));
        TranslateErrorService translateErrorService = Spider.getDefaultExtension(TranslateErrorService.class);
        translateErrorService.translateError(result);
        return JsonUtils.toJSONString(result);
    }

    private String resolveModuleName(String moduleName, HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("module"))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .orElse(moduleName);
    }

    private void clear() {
        PamirsSession.clear();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        contextIsRunning = true;
    }
}
