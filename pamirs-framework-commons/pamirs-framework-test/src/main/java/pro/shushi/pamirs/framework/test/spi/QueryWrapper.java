package pro.shushi.pamirs.framework.test.spi;

import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * Entity 对象封装操作类
 */
@SuppressWarnings("serial")
public class QueryWrapper<T> extends IWrapper<T> {

    @Override
    public T getEntity() {
        return null;
    }

    @Override
    public IWrapper<DataMap> generic(DataMap entity) {
        return null;
    }

    @Override
    public IWrapper<DataMap> generic(String model, DataMap entity) {
        return null;
    }

}
