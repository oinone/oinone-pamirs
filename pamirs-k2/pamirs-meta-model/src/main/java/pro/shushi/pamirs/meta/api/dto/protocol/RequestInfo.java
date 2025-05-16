package pro.shushi.pamirs.meta.api.dto.protocol;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.enmu.CheckStrategyEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 请求基本信息
 * <p>
 * 2021/3/12 4:51 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class RequestInfo implements PamirsRequestInfoConstants {

    private Map<String, Object> requestInfoMap;

    public static Map<String, Object> init(Map<String, Object> requestInfoMap) {
        if (null == requestInfoMap) {
            requestInfoMap = new HashMap<>();
        }
        // 设置默认值
        requestInfoMap.putIfAbsent(REQUEST_STRATEGY_CHECK_STRATEGY, CheckStrategyEnum.RETURN_WHEN_COMPLETED);
        requestInfoMap.putIfAbsent(REQUEST_STRATEGY_MSG_LEVEL, InformationLevelEnum.ERROR);
        return requestInfoMap;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Enum> void convertEnum(Map<String, Object> requestInfoMap, Class<T> enumClass, String key) {
        Object value = requestInfoMap.get(key);
        if (null == value) {
            return;
        }
        if (String.class.isAssignableFrom(value.getClass())) {
            requestInfoMap.put(key, Enum.valueOf(enumClass, (String) value));
        }
    }

    protected RequestInfo setRequestInfoMap(Map<String, Object> requestInfoMap) {
        this.requestInfoMap = requestInfoMap;
        if (null != requestInfoMap) {
            convertEnum(requestInfoMap, CheckStrategyEnum.class, REQUEST_STRATEGY_CHECK_STRATEGY);
            convertEnum(requestInfoMap, InformationLevelEnum.class, REQUEST_STRATEGY_MSG_LEVEL);
        }
        return this;
    }

    private Object get(String key) {
        return Optional.ofNullable(requestInfoMap).map(v -> v.get(key)).orElse(null);
    }

    public RequestInfo set(String key, Object value) {
        if (null != requestInfoMap && null != value) {
            requestInfoMap.put(key, value);
        }
        return this;
    }

    private String getString(String key) {
        return (String) Optional.ofNullable(requestInfoMap).map(v -> v.get(key)).orElse(null);
    }

    public RequestInfo setString(String key, String value) {
        if (null != requestInfoMap && StringUtils.isNotBlank(value)) {
            requestInfoMap.put(key, value);
        }
        return this;
    }

    private boolean getBoolean(String key) {
        Boolean result = (Boolean) Optional.ofNullable(requestInfoMap).map(v -> v.get(key)).orElse(null);
        return null != result && result;
    }

    public String getProduct() {
        return getString(REQUEST_PRODUCT);
    }

    public RequestInfo setProduct(String product) {
        return setString(REQUEST_PRODUCT, product);
    }

    public String getApp() {
        return getString(REQUEST_APP);
    }

    public RequestInfo setApp(String app) {
        return setString(REQUEST_APP, app);
    }

    public String getModel() {
        return getString(REQUEST_MODEL);
    }

    public RequestInfo setModel(String model) {
        return setString(REQUEST_MODEL, model);
    }

    public CheckStrategyEnum getCheckStrategy() {
        return (CheckStrategyEnum) get(REQUEST_STRATEGY_CHECK_STRATEGY);
    }

    public RequestInfo setCheckStrategy(CheckStrategyEnum checkStrategy) {
        return set(REQUEST_STRATEGY_CHECK_STRATEGY, checkStrategy);
    }

    public InformationLevelEnum getMsgLevel() {
        return (InformationLevelEnum) get(REQUEST_STRATEGY_MSG_LEVEL);
    }

    public RequestInfo setMsgLevel(InformationLevelEnum msgLevel) {
        return set(REQUEST_STRATEGY_MSG_LEVEL, msgLevel);
    }

    public boolean isOnlyValidate() {
        return getBoolean(REQUEST_STRATEGY_ONLY_VALIDATE);
    }

    public RequestInfo setOnlyValidate(boolean onlyValidate) {
        return set(REQUEST_STRATEGY_ONLY_VALIDATE, onlyValidate);
    }

}
