package pro.shushi.pamirs.framework.gateways.graph.java.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.RequestHelper;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.io.IOException;

@Component
public class RequestSessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        try {
            PamirsSession.clear();
            RequestHelper.getSessionPrepareApi().prepare(httpServletRequest, RequestHelper.getHeaderModuleName(httpServletRequest), new PamirsRequestParam().setVariables(new PamirsRequestVariables()));
            chain.doFilter(request, response);
        } finally {
            PamirsSession.clear();
        }
    }

}