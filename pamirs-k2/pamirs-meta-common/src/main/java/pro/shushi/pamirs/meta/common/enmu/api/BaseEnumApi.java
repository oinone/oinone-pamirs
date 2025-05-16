package pro.shushi.pamirs.meta.common.enmu.api;

import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 枚举底层接口
 * 2021/1/11 9:12 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface BaseEnumApi {

    /**
     * 从枚举类获取数据字典编码
     *
     * @param enumClass 枚举类
     * @return 返回值
     */
    String fetchDictionaryFromClass(Class<?> enumClass);

}
