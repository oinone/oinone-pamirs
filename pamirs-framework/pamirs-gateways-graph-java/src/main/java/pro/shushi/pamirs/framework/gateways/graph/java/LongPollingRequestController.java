//package pro.shushi.pamirs.framework.gateways.graph.java;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.async.DeferredResult;
//import pro.shushi.pamirs.framework.gateways.graph.domain.PamirsRequestParam;
//import pro.shushi.pamirs.framework.gateways.graph.domain.RequestVariable;
//import pro.shushi.pamirs.framework.gateways.graph.java.request.RequestExecutor;
//import pro.shushi.pamirs.framework.gateways.graph.longpolling.PamirsLongPolling;
//import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
//import pro.shushi.pamirs.meta.api.session.PamirsSession;
//import pro.shushi.pamirs.meta.util.JsonUtils;
//
//import jakarta.annotation.Resource;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Map;
//
//@RestController
//@Slf4j
//public class LongPollingRequestController {
//
//    @Resource
//    RequestExecutor RequestExecutor;
//
//    @RequestMapping(value = "/pamirs/longpolling/{moduleName}", method = RequestMethod.GET)
//    public DeferredResult<String> pamirsLongPollingGet(@PathVariable("moduleName") String moduleName,
//                                                       @RequestParam("query") String gql,
//                                                       @RequestParam("variables") Map<String, Object> variables,
//                                                       HttpServletRequest request,
//                                                       HttpServletResponse response) throws IOException {
//        PamirsRequestParam gqlRequest = new PamirsRequestParam();
//        gqlRequest.setQuery(gql);
//        gqlRequest.setVariables(variables);
//        return pamirsLongPollingPost(moduleName, gqlRequest, request, response);
//    }
//
//    @RequestMapping(value = "/pamirs/longpolling/{moduleName}", method = RequestMethod.POST)
//    public DeferredResult<String> pamirsLongPollingPost(@PathVariable("moduleName") String moduleName,
//                                                        @RequestBody PamirsRequestParam gql,
//                                                        HttpServletRequest request,
//                                                        HttpServletResponse response) throws IOException {
//        destroy();
//        prepare(request, moduleName, gql);
//        return execute(moduleName, model, fun, gql);
//    }
//
//    private DeferredResult<String> execute(String moduleName, String model, String fun,
//                                                   PamirsRequestParam gql) {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        PamirsLongPolling longPolling = new PamirsLongPolling(gql.getVariables(), gql.getQuery()) {
//            @Override
//            protected String execute() {
//                RequestContextHolder.setRequestAttributes(requestAttributes);
//                return JsonUtils.toJSONString(RequestExecutor.execute(moduleName, gql));
//            }
//        };
//        destroy();
//        return longPolling.getDeferredResult();
//
//
//    }
//
//
//    private void destroy() {
//        PamirsSession.removeThreadLocal();
//    }
//
//    private void prepare(HttpServletRequest request, String moduleName, PamirsRequestParam gql) {
//        RequestVariable pv = new RequestVariable(gql.getVariables());
//        PamirsEnvironment.setRequestVariable(pv);
//        String userLang = request.getHeader("accept-language");
//        String s = StringUtils.substringBefore(userLang, ",");
//        PamirsEnvironment.getThreadLocal().setLang(s);
//        String pamirsHost = request.getHeader("pamirs-host");
//        if (StringUtils.isNotBlank(pamirsHost) && StringUtils.isBlank(PamirsEnvironment.getThreadLocal().getHost())) {
//            PamirsEnvironment.getThreadLocal().setHost(pamirsHost);
//        }
//        prepareTenancy(moduleName);
//    }
//
//    private void prepareTenancy(String moduleName) {
//        String tenancyCode = NetUtils.getSubDomain();
//        Boolean isAllow = RequestCheck.checkVisitRule(moduleName, tenancyCode);
//        //Boolean isAllow = true;
//        if (!isAllow) {
//            throw log.error(ExpEnumerate.BASE_CHECK_VISIT_MODULE_RULE_ERROR, "{} concurrent tenancy has no rule enter the module", tenancyCode).errThrow();
//        }
//        /**设置二级域名为租户 存储环境变量*/
//        PamirsEnvironment.getThreadLocal().setTenancy(tenancyCode);
//    }
//
//}
