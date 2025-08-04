package pro.shushi.pamirs.framework.common.spi.service;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.spi.FetchRequestClientTypeApi;
import pro.shushi.pamirs.framework.common.spi.RequestFunctionPrepareApi;
import pro.shushi.pamirs.framework.common.spi.SessionInitApi;
import pro.shushi.pamirs.framework.common.spi.SessionPrepareApi;
import pro.shushi.pamirs.framework.common.utils.CookieUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestInfoConstants;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * session构造模板方法
 * <p>
 * 2022/8/30 10:36 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class SessionPrepareTemplate implements SessionPrepareApi, SessionPrepareTemplateApi {

    private static final HoldKeeper<FetchRequestClientTypeApi> fetchRequestClientTypeHolder = new HoldKeeper<>();

    private static FetchRequestClientTypeApi getFetchRequestClientTypeApi() {
        return fetchRequestClientTypeHolder.supply(() -> Spider.getDefaultExtension(FetchRequestClientTypeApi.class));
    }

    @Override
    public void prepare(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {
        before(request, moduleName, requestParam);

        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headerMap.put(headerName, headerValue);
        }
        requestParam.getVariables().setHeaders(headerMap);
        requestParam.getVariables().putAllParameterMap(request.getParameterMap());
        requestParam.getVariables().setRequestUrl(request.getRequestURL().toString());
        requestParam.getVariables().setTraceId(UUID.randomUUID().toString());

        String sessionId = CookieUtil.getValue(request, CookieUtil.USER_SESSION_ID);
        if (StringUtils.isNotBlank(sessionId)) {
            PamirsSession.setSessionId(sessionId);
        }
        PamirsSession.setRequestVariables(requestParam.getVariables());
        String product = (String) PamirsSession.getRequestInfo(PamirsRequestInfoConstants.REQUEST_PRODUCT);
        PamirsSession.setProduct(product);

        requestParam.getVariables().setClientType(getFetchRequestClientTypeApi().fetchCurrentClientType(request, moduleName, requestParam));

        injection(request, moduleName, requestParam);

        if (StringUtils.isNotBlank(moduleName)) {
            PamirsSession.setServApp(getModuleDefinitionModule(moduleName));
        }
        String requestFromModuleName = request.getHeader(VariableNameConstants.module);
        if (StringUtils.isNotBlank(requestFromModuleName)) {
            PamirsSession.setRequestFromModule(getModuleDefinitionModule(requestFromModuleName));
        }

        String app = (String) PamirsSession.getRequestInfo(PamirsRequestInfoConstants.REQUEST_APP);
        if (StringUtils.isNotBlank(app)) {
            ModuleDefinition moduleDefinition = PamirsSession.getContext().getModuleCache().getByName(app);
            if (null != moduleDefinition) {
                PamirsSession.setAppId(moduleDefinition.getModule());
                PamirsSession.setAppName(app);
            }
        }

        String companyCode = (String) PamirsSession.getRequestInfo("companyCode");
        if (StringUtils.isNotBlank(companyCode)) {
            PamirsSession.getTransmittableExtend().put("companyCode", companyCode);
        }

        Spider.getDefaultExtension(RequestFunctionPrepareApi.class).prepare(request, requestParam);
        after(request, moduleName, requestParam);
    }

    @Override
    public void after(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {
        for (SessionInitApi api : BeanDefinitionUtils.getBeansOfTypeByOrdered(SessionInitApi.class)) {
            api.init(request, moduleName, requestParam);
        }
    }

    private String getModuleDefinitionModule(String moduleName) {
        ModuleDefinition moduleDefinition = PamirsSession.getContext().getModuleCache().getByName(moduleName);
        if (null == moduleDefinition) {
            log.warn("Invalid module name. {}", moduleName);
            return null;
        }
        return moduleDefinition.getModule();
    }
}
