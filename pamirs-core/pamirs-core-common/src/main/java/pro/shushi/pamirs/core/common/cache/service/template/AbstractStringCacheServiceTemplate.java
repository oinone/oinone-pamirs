package pro.shushi.pamirs.core.common.cache.service.template;

import com.alibaba.fastjson.JSONObject;
import pro.shushi.pamirs.meta.util.JsonUtils;

/**
 * 抽象字符串缓存服务模板
 *
 * @author Adamancy Zhang at 16:48 on 2021-06-20
 */
public abstract class AbstractStringCacheServiceTemplate<T> extends AbstractCacheServiceTemplate<T, String> {

    /**
     * 获取目标类
     *
     * @return 目标类
     */
    protected abstract Class<T> fetchTargetClass();

    @Override
    protected String serializable(String key, T data) {
        return JsonUtils.toJSONString(data);
    }

    @Override
    protected T deserialization(String key, String data) {
        if (JSONObject.isValidObject(data)) {
            return JsonUtils.parseObject(data, fetchTargetClass());
        } else {
            setEmptyObject(key);
            return null;
        }
    }

    @Override
    protected void setEmptyObject(String key) {
        setCacheData(key, EMPTY_FLAG);
    }

    @Override
    protected boolean isEmptyObject(String key, Object data) {
        return EMPTY_FLAG.equals(data);
    }
}
