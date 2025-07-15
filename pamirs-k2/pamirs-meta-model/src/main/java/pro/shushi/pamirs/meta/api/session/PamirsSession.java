package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.api.core.session.SessionApi;
import pro.shushi.pamirs.meta.api.core.session.SessionClearService;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
import pro.shushi.pamirs.meta.api.dto.protocol.EnvEnum;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestInfoConstants;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * pamirs session
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:45 下午
 */
public class PamirsSession extends PamirsRequestSession {

    public static final String SESSION_PRODUCT = "PRODUCT";
    public static final String SESSION_ID = "SESSION_ID";
    public static final String SESSION_APP_ID = "APP_ID";
    public static final String SESSION_APP_NAME = "APP_NAME";
    public static final String SESSION_SERV_APP = "SERV_APP";
    public static final String SESSION_ENV = "ENV";
    public static final String SESSION_LANG = "LANG";
    public static final String SESSION_COUNTRY = "COUNTRY";
    public static final String SESSION_USER_ID = "USER_ID";
    public static final String SESSION_USER_CODE = "USER_CODE";
    public static final String SESSION_USER_NAME = "USER_NAME";
    public static final String SESSION_ADMIN_TAG = "ADMIN_TAG";

    public static final String REQUEST_VARIABLES = "REQUEST_VARIABLES";
    public static final String SESSION_DIRECTIVE = "DIRECTIVE";
    public static final String SESSION_STATIC_CONFIG = "STATIC_CONFIG";
    public static final String SESSION_BATCH_OPERATION = "BATCH_OPERATION";

    public static final String SESSION_KERNEL_EXTEND = "KERNEL_EXTEND";
    public static final String SESSION_TRANSMITTABLE_EXTEND = "TRANSMITTABLE_EXTEND";

    private static final HoldKeeper<SessionApi> holder = new HoldKeeper<>();

    private static final HoldKeeper<SessionClearService> clearServiceHolder = new HoldKeeper<>();

    public static String getEnv() {
        SessionApi sessionApi = getSessionApi();
        String env = sessionApi.getEnv();
        if (null == env) {
            return EnvEnum.product.toString();
        }
        return env;
    }

    public static void setEnv(String env) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setEnv(env);
    }

    public static boolean isPreview() {
        return EnvEnum.preview.toString().equals(getEnv());
    }

    public static void setPreview(boolean isPreview) {
        if (isPreview) {
            setEnv(EnvEnum.preview.toString());
        } else {
            setEnv(EnvEnum.product.toString());
        }
    }

    public static String getProduct() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getProduct();
    }

    public static void setProduct(String appId) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setProduct(appId);
    }

    public static String getAppId() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getAppId();
    }

    public static void setAppId(String appId) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setAppId(appId);
    }

    public static String getSessionId() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getSessionId();
    }

    public static void setSessionId(String sessionId) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setSessionId(sessionId);
    }

    public static String getAppName() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getAppName();
    }

    public static void setAppName(String appName) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setAppName(appName);
    }

    public static String getServApp() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getServApp();
    }

    public static void setServApp(String appName) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setServApp(appName);
    }

    public static String getRequestFromModule() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getRequestFromModule();
    }

    public static void setRequestFromModule(String requestFromModule) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setRequestFromModule(requestFromModule);
    }

    public static <T extends Serializable> T getUserId() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getUserId();
    }

    public static <T extends Serializable> void setUserId(T userId) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setUserId(userId);
    }

    public static String getUserName() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getUserName();
    }

    public static void setUserName(String userName) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setUserName(userName);
    }

    public static String getUserCode() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getUserCode();
    }

    public static void setUserCode(String userCode) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setUserCode(userCode);
    }

    public static Boolean isAdmin() {
        return getSessionApi().isAdmin();
    }

    public static void setIsAdmin(Boolean isAdmin) {
        getSessionApi().setIsAdmin(isAdmin);
    }

    public static Boolean isAnonymous() {
        return getSessionApi().isAnonymous();
    }

    public static void setIsAnonymous(Boolean isAnonymous) {
        getSessionApi().setIsAnonymous(isAnonymous);
    }

    public static String getLang() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getLang();
    }

    public static void setLang(String lang) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setLang(lang);
    }

    public static String getCountry() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getCountry();
    }

    public static void setCountry(String country) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setCountry(country);
    }

    public static Map<String, String> getTransmittableExtend() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getTransmittableExtend();
    }

    public static void setTransmittableExtend(Map<String, String> extend) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setTransmittableExtend(extend);
    }

    public static Object getDsKey() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getDsKey();
    }

    public static void pushDsKey(Object dsKey) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.pushDsKey(dsKey);
    }

    public static void clearDsKey() {
        SessionApi sessionApi = getSessionApi();
        sessionApi.popDsKey();
    }

    public static String getAsProperty() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getAsProperty();
    }

    public static void pushAsProperty(String model) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.pushAsProperty(model);
    }

    public static String clearAsProperty() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.popAsProperty();
    }

    public static Integer getBatchSize() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getBatchSize();
    }

    public static void pushBatchSize(Integer batchSize) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.pushBatchSize(batchSize);
    }

    public static void clearBatchSize() {
        SessionApi sessionApi = getSessionApi();
        sessionApi.popBatchSize();
    }

    public static BatchCommitTypeEnum getBatchOperation() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getBatchOperation();
    }

    public static void setBatchOperation(BatchCommitTypeEnum batchOperation) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setBatchOperation(batchOperation);
    }

    public static MessageHub getMessageHub() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getMessageHub();
    }

    public static void setMessageHub(MessageHub messageHub) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setMessageHub(messageHub);
    }

    public static SessionMetaBit directive() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.directive();
    }

    public static PamirsRequestVariables getRequestVariables() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getRequestVariables();
    }

    public static void setRequestVariables(PamirsRequestVariables requestVariables) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setRequestVariables(requestVariables);
    }

    /**
     * 获取请求上下文变量
     *
     * @param name 变量名
     * @return 变量值
     * @see PamirsRequestInfoConstants
     */
    public static Object getRequestInfo(String name) {
        return Optional.ofNullable(getRequestVariables())
                .map(PamirsRequestVariables::getRequestInfoMap)
                .map(v -> v.get(name))
                .orElse(null);
    }

    public static boolean isStaticConfig() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.isStaticConfig();
    }

    public static void setStaticConfig(boolean staticConfig) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setStaticConfig(staticConfig);
    }

    public static Map<String, String> getKernelExtend() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.getKernelExtend();
    }

    public static void setKernelExtend(Map<String, String> extend) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.setKernelExtend(extend);
    }

    public static Map<String, String> fetchSessionMap() {
        SessionApi sessionApi = getSessionApi();
        return sessionApi.fetchSessionMap();
    }

    public static void fillSessionFromMap(Map<String, String> sessionMap) {
        SessionApi sessionApi = getSessionApi();
        sessionApi.fillSessionFromMap(sessionMap);
    }

    public static void clearMainSession() {
        getSessionClearService().clearMainSession();
    }

    public static void clearSubSession() {
        getSessionClearService().clearSubSession();
    }

    public static void clear() {
        getSessionClearService().clear();
    }

    public static SessionApi getSessionApi() {
        return holder.supply(() -> Spider.getDefaultExtension(SessionApi.class));
    }

    public static SessionClearService getSessionClearService() {
        return clearServiceHolder.supply(() -> Spider.getDefaultExtension(SessionClearService.class));
    }

    @Deprecated
    public static Boolean getAdminTag() {
        return isAdmin();
    }

    @Deprecated
    public static void setAdminTag(Boolean adminTag) {
        setIsAdmin(adminTag);
    }
}
