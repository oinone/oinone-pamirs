package pro.shushi.pamirs.meta.api.core.orm;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 对象关系映射API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Order
@SPI
public interface OrmApi {

    /**
     * 给模型设置对应的model变量
     *
     * @param <T>   模型类型
     * @param model 模型编码
     * @param obj   obj
     * @return 模型
     */
    <T> Object modeling(String model, T obj);

    /**
     * 模型转Map
     *
     * @param model 模型编码
     * @param obj   obj
     * @return map
     */
    <T> T mapping(String model, Object obj);

    /**
     * Map转模型
     *
     * @param model 模型编码
     * @param map   map
     * @return 模型
     */
    <T> T objecting(String model, Object map);

}
