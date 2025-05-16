package pro.shushi.pamirs.resource.api.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.List;

public interface SingletonModel<T extends BaseModel> {

    @SuppressWarnings("unchecked")
    default T singletonModel() {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String value = redisTemplate.opsForValue().get(getKey());
        T t = null;
        if (StringUtils.isNotBlank(value)) {
            t = (T) JsonUtils.parseObject(value, this.getClass());
        }
        if (t == null) {
            QueryWrapper<T> wrapper = Pops.<T>query().from(this.getClass());
            List<T> tList = new BaseModel().queryList(wrapper);
            if (tList == null || tList.size() == 0) {
                return null;
            } else {
                t = tList.get(0);
                redisTemplate.opsForValue().set(getKey(), JsonUtils.toJSONString(t));
                initSystem();
            }
        }
        return t;
    }

    default void cleanCache() {
        BeanDefinitionUtils.getBean(StringRedisTemplate.class).delete(getKey());
    }

    void initSystem();

    default String getKey() {
        return this.getClass().getName();
    }

}

