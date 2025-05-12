package pro.shushi.pamirs.meta.api.dto.protocol;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.enmu.CheckStrategyEnum;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 请求参数上下文变量
 * <p>
 * 请求中不可变更
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Data
public class PamirsRequestVariables implements PamirsRequestInfoConstants, Serializable {

    private static final long serialVersionUID = 8765069249289823394L;

    private String requestUrl;

    private String traceId;

    /**
     * 请求客户端类型
     */
    private ClientTypeEnum clientType;

    private Map<String, Object> variables;

    private Map<String, String[]> parameterMap;

    private Map<String, String> headers;

    private Map<String, Object> requestInfoMap;

    @JSONField(serialize = false)
    private transient RequestInfo requestInfo;

    // https://blog.csdn.net/I_am_hardy/article/details/123947516
    // 前端传参是首字母大写的，到HttpServlet就变成小写了。
    // http协议header字段名大小写不敏感的，所以每个框架的写法可能都不一样，如果需要判断请求头相等需要都考虑，做忽略大小写判断
    public String getHeader(String name) {
        if (this.getHeaders() == null) {
            return null;
        }
        return this.getHeaders().get(name.toLowerCase());
    }

    @Nullable
    public String getParameter(String name) {
        if (this.getParameterMap() == null) {
            return null;
        }
        String[] values = this.getParameterMap().get(name);
        if (values != null) {
            return values.length > 0 ? values[0] : null;
        } else {
            return null;
        }
    }

    public void putAllParameterMap(Map<String, String[]> parameterMap) {
        if (null == this.getParameterMap()) {
            this.setParameterMap(new HashMap<>());
        }
        this.getParameterMap().putAll(parameterMap);
    }

    public URI getURI() {
        if (StringUtils.isBlank(requestUrl)) {
            return null;
        }
        return URI.create(requestUrl);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRequestInfoMap() {
        if (null == requestInfoMap) {
            synchronized (this) {
                if (null == requestInfoMap) {
                    Map<String, Object> apiInfo = Optional.ofNullable(variables)
                            .map(_v -> (Map<String, Object>) _v.get(PamirsRequestInfoConstants.REQUEST))
                            .orElse(null);
                    requestInfoMap = RequestInfo.init(apiInfo);
                }
            }
        }
        return requestInfoMap;
    }

    public RequestInfo getRequestInfo() {
        if (null == requestInfo) {
            synchronized (this) {
                if (null == requestInfo) {
                    requestInfo = new RequestInfo().setRequestInfoMap(getRequestInfoMap());
                }
            }
        }
        return requestInfo;
    }

    public static RequestInfo parseRequestInfo(String info) {
        if (StringUtils.isBlank(info)) {
            return null;
        }
        return new RequestInfo().setRequestInfoMap(JsonUtils.parseMap(info));
    }

    public boolean returnWhenError() {
        RequestInfo requestStrategy = getRequestInfo();
        CheckStrategyEnum checkStrategy = null == requestStrategy
                ? CheckStrategyEnum.RETURN_WHEN_ERROR : requestStrategy.getCheckStrategy();
        return !CheckStrategyEnum.RETURN_WHEN_COMPLETED.equals(checkStrategy);
    }

}
