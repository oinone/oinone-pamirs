package pro.shushi.pamirs.meta.api.core.orm.systems;

import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型检查接口
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface PopsApi {

    <T> IWrapper<T> construct(String model);

}
