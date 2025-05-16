package pro.shushi.pamirs.framework.orm.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.serialize.SerializeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.SerializeApi;

import javax.annotation.Resource;

/**
 * 持久化序列化服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component("persistenceSerializeApi")
public class PersistenceSerializeApi implements SerializeApi {

    @Resource
    private SerializeTemplate serializeTemplate;

    @Override
    public <T> T serialize(String model, T origin) {
        return serializeTemplate.serialize(model, origin, Boolean.TRUE);
    }

    @Override
    public <T> T deserialize(String model, T origin) {
        return serializeTemplate.deserialize(model, origin, Boolean.TRUE);
    }

}
