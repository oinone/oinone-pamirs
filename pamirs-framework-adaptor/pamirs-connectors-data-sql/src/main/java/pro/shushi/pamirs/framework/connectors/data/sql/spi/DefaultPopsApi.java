package pro.shushi.pamirs.framework.connectors.data.sql.spi;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.core.orm.systems.PopsApi;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型校验接口默认实现
 * <p>
 * 2020/7/1 8:37 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(99)
@SPI.Service
public class DefaultPopsApi implements PopsApi {

    @Override
    public <T> IWrapper<T> construct(String model) {
        return Pops.<T>query().setModel(model);
    }

}
