package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * pamirs线程变量
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:41 下午
 */
public class PamirsThreadLocal<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -7154481442796541819L;

    private String env;

    private String product;

    private String sessionId;

    private String appId;

    private String appName;

    private String servApp;

    private String requestFromModule;

    private T userId;

    private String userCode;

    @Deprecated
    private Boolean adminTag;

    private Boolean isAdmin;

    private Boolean isAnonymous;

    private String userName;

    private String lang;

    private String country;

    private RequestContext context = new RequestContext();

    private PamirsRequestVariables variables = new PamirsRequestVariables();

    private Map<String, String> extend = new ConcurrentHashMap<>();

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        if (null != this.env) {
            return;
        }
        this.env = env;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServApp() {
        return servApp;
    }

    public void setServApp(String servApp) {
        this.servApp = servApp;
    }

    public String getRequestFromModule() {
        return requestFromModule;
    }

    public void setRequestFromModule(String requestFromModule) {
        this.requestFromModule = requestFromModule;
    }

    public T getUserId() {
        return userId;
    }

    public void setUserId(T userId) {
        if (null != this.userId) {
            return;
        }
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.adminTag = isAdmin;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @SuppressWarnings("unused")
    public void clearContext() {
        context = new RequestContext();
    }

    public RequestContext getContext() {
        return context;
    }

    public void setContext(RequestContext context) {
        this.context = context;
    }

    public PamirsRequestVariables getVariables() {
        return variables;
    }

    public void setVariables(PamirsRequestVariables variables) {
        this.variables = variables;
    }

    public Map<String, String> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, String> extend) {
        this.extend = extend;
    }

    @Deprecated
    public Boolean getAdminTag() {
        return adminTag;
    }

    @Deprecated
    public void setAdminTag(Boolean adminTag) {
        this.adminTag = adminTag;
    }
}
