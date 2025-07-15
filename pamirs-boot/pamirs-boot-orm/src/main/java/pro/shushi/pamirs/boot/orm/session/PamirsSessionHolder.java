package pro.shushi.pamirs.boot.orm.session;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.session.RequestSessionApi;
import pro.shushi.pamirs.meta.api.core.session.SessionApi;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.api.core.session.watch.*;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
import pro.shushi.pamirs.meta.api.dto.protocol.EnvEnum;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.api.session.PamirsKernelThreadLocal;
import pro.shushi.pamirs.meta.api.session.PamirsThreadLocal;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionCacheFactoryApi;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.spi.Holder;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * pamirs session holder
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:45 下午
 */
@Order(88)
@SPI.Service
@Component
public class PamirsSessionHolder implements SessionApi, RequestSessionApi, SessionClearApi, AutoCloseable {

    @SuppressWarnings("rawtypes")
    private final static TransmittableThreadLocal<PamirsThreadLocal> _thread = new TransmittableThreadLocal<>();

    private final static ThreadLocal<PamirsKernelThreadLocal> _k_thread = new ThreadLocal<>();

    @Resource
    private SessionCacheFactoryApi defaultSessionCacheFactoryApi;

    @Override
    public String getEnv() {
        init();
        return _thread.get().getEnv();
    }

    @Override
    public void setEnv(String env) {
        init();
        String current = _thread.get().getEnv();
        watch(env, current, EnvWatcher.class);
        _thread.get().setEnv(env);
    }

    @Override
    public Boolean isPreview() {
        init();
        return EnvEnum.preview.toString().equals(_thread.get().getEnv());
    }

    @Override
    public String getProduct() {
        init();
        return _thread.get().getProduct();
    }

    @Override
    public void setProduct(String product) {
        init();
        _thread.get().setProduct(product);
    }

    @Override
    public String getSessionId() {
        init();
        String sessionId = _thread.get().getSessionId();
        if (StringUtils.isBlank(sessionId)) {
            sessionId = UUIDUtil.getUUIDNumberString();
            setSessionId(sessionId);
        }
        return sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        init();
        _thread.get().setSessionId(sessionId);
    }

    @Override
    public String getAppId() {
        init();
        return _thread.get().getAppId();
    }

    @Override
    public void setAppId(String appId) {
        init();
        String current = _thread.get().getEnv();
        watch(appId, current, AppIdWatcher.class);
        _thread.get().setAppId(appId);
    }

    @Override
    public String getAppName() {
        init();
        return _thread.get().getAppName();
    }

    @Override
    public void setAppName(String appName) {
        init();
        _thread.get().setAppName(appName);
    }

    @Override
    public String getServApp() {
        init();
        return _thread.get().getServApp();
    }

    @Override
    public void setServApp(String appName) {
        init();
        _thread.get().setServApp(appName);
    }

    @Override
    public String getRequestFromModule() {
        init();
        return _thread.get().getRequestFromModule();
    }

    @Override
    public void setRequestFromModule(String requestFromModule) {
        init();
        _thread.get().setRequestFromModule(requestFromModule);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Serializable> T getUserId() {
        init();
        return (T) _thread.get().getUserId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Serializable> void setUserId(T userId) {
        init();
        String current = _thread.get().getEnv();
        watch(userId, current, UserIdWatcher.class);
        _thread.get().setUserId(userId);
    }

    @Override
    public String getUserName() {
        init();
        return _thread.get().getUserName();
    }

    @Override
    public void setUserName(String userName) {
        init();
        String current = _thread.get().getEnv();
        watch(userName, current, UserNameWatcher.class);
        _thread.get().setUserName(userName);
    }

    @Override
    public String getUserCode() {
        init();
        return _thread.get().getUserCode();
    }

    @Override
    public void setUserCode(String userCode) {
        init();
        String current = _thread.get().getEnv();
        watch(userCode, current, UserCodeWatcher.class);
        _thread.get().setUserCode(userCode);
    }

    @Override
    public Boolean isAdmin() {
        init();
        return _thread.get().getIsAdmin();
    }

    @Override
    public void setIsAdmin(Boolean isAdmin) {
        init();
        _thread.get().setIsAdmin(isAdmin);
    }

    @Override
    public Boolean isAnonymous() {
        init();
        return _thread.get().getIsAnonymous();
    }

    @Override
    public void setIsAnonymous(Boolean isAnonymous) {
        init();
        _thread.get().setIsAnonymous(isAnonymous);
    }

    @Override
    public String getLang() {
        init();
        return _thread.get().getLang();
    }

    @Override
    public void setLang(String lang) {
        init();
        String current = _thread.get().getEnv();
        watch(lang, current, LangWatcher.class);
        _thread.get().setLang(lang);
    }

    @Override
    public String getCountry() {
        init();
        return _thread.get().getCountry();
    }

    @Override
    public void setCountry(String country) {
        init();
        String current = _thread.get().getEnv();
        watch(country, current, CountryWatcher.class);
        _thread.get().setCountry(country);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getTransmittableExtend() {
        init();
        return _thread.get().getExtend();
    }

    @Override
    public void setTransmittableExtend(Map<String, String> extend) {
        init();
        _thread.get().setExtend(extend);
    }

    @Override
    public Object getDsKey() {
        kernelInit();
        return _k_thread.get().getDsKey();
    }

    @Override
    public Object popDsKey() {
        kernelInit();
        return _k_thread.get().popDsKey();
    }

    @Override
    public void pushDsKey(Object dsKey) {
        kernelInit();
        _k_thread.get().pushDsKey(dsKey);
    }

    @Override
    public String getAsProperty() {
        kernelInit();
        return _k_thread.get().getAsProperty();
    }

    @Override
    public String popAsProperty() {
        kernelInit();
        return _k_thread.get().popAsProperty();
    }

    @Override
    public void pushAsProperty(String model) {
        kernelInit();
        _k_thread.get().pushAsProperty(model);
    }

    @Override
    public Integer getBatchSize() {
        kernelInit();
        return _k_thread.get().getBatchSize();
    }

    @Override
    public Integer popBatchSize() {
        kernelInit();
        return _k_thread.get().popBatchSize();
    }

    @Override
    public void pushBatchSize(Integer batchSize) {
        kernelInit();
        _k_thread.get().pushBatchSize(batchSize);
    }

    @Override
    public BatchCommitTypeEnum getBatchOperation() {
        kernelInit();
        return _k_thread.get().getBatchOperation();
    }

    @Override
    public void setBatchOperation(BatchCommitTypeEnum operation) {
        kernelInit();
        _k_thread.get().setBatchOperation(operation);
    }

    @Override
    public MessageHub getMessageHub() {
        kernelInit();
        return _k_thread.get().getMessageHub();
    }

    @Override
    public void setMessageHub(MessageHub messageHub) {
        kernelInit();
        _k_thread.get().setMessageHub(messageHub);
    }

    @Override
    public SessionMetaBit directive() {
        kernelInit();
        return _k_thread.get();
    }

    private static final Holder<RequestContext> requestContextHolder = new Holder<>();

    private static final ReentrantReadWriteLock contextLock = new ReentrantReadWriteLock();

    private static final Lock contextWriteLock = contextLock.writeLock();

    @Override
    public RequestContext getContext() {
        RequestContext requestContext = requestContextHolder.get();
        if (requestContext == null) {
            synchronized (requestContextHolder) {
                requestContext = requestContextHolder.get();
                if (requestContext == null) {
                    requestContext = new RequestContext().init(defaultSessionCacheFactoryApi);
                    requestContextHolder.set(requestContext);
                }
            }
        }
        return requestContext;
    }

    @Override
    public void setContext(RequestContext context) {
        contextWriteLock.lock();
        try {
            requestContextHolder.set(context);
        } finally {
            contextWriteLock.unlock();
        }
    }

    @Override
    public PamirsRequestVariables getRequestVariables() {
        init();
        return _thread.get().getVariables();
    }

    @Override
    public void setRequestVariables(PamirsRequestVariables requestVariables) {
        init();
        PamirsRequestVariables current = _thread.get().getVariables();
        watch(requestVariables, current, RequestVariablesWatcher.class);
        _thread.get().setVariables(requestVariables);
    }

    @Override
    public boolean isStaticConfig() {
        kernelInit();
        return _k_thread.get().isStaticConfig();
    }

    @Override
    public void setStaticConfig(boolean staticConfig) {
        kernelInit();
        _k_thread.get().setStaticConfig(staticConfig);
    }

    @Override
    public Map<String, String> getKernelExtend() {
        kernelInit();
        return _k_thread.get().getExtend();
    }

    @Override
    public void setKernelExtend(Map<String, String> extend) {
        kernelInit();
        _k_thread.get().setExtend(extend);
    }

    @Override
    public void clear() {
        _thread.remove();
        _k_thread.remove();
    }

    private <T extends Serializable> void init() {
        if (null == _thread.get()) {
            _thread.set(new PamirsThreadLocal<T>());
        }
        if (null == _thread.get().getContext()) {
            _thread.get().setContext(new RequestContext());
        }
    }

    private void kernelInit() {
        if (null == _k_thread.get()) {
            _k_thread.set(new PamirsKernelThreadLocal());
        }
    }

    @Override
    public void close() {
        clear();
    }

    private <V, T extends SessionWatcher<V>> void watch(V newValue, V current, Class<T> watch) {
        if (newValue instanceof PamirsRequestVariables || null != newValue && !newValue.equals(current)) {
            List<T> watchers = Spider.getLoader(watch).getOrderedExtensions();
            if (!CollectionUtils.isEmpty(watchers)) {
                for (T orderedExtension : watchers) {
                    orderedExtension.watch(current, newValue);
                }
            }
        }
    }

    @Deprecated
    @Override
    public Boolean getAdminTag() {
        return isAdmin();
    }

    @Deprecated
    @Override
    public void setAdminTag(Boolean adminTag) {
        setIsAdmin(adminTag);
    }
}

