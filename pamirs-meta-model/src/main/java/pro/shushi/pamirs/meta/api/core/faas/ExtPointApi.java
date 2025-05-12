package pro.shushi.pamirs.meta.api.core.faas;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 扩展点API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExtPointApi {

    /**
     * 执行扩展点
     *
     * @param namespace    模型编码
     * @param extPointName 扩展点名称
     * @param args         入参
     * @return 结果
     */
    <T> T run(String namespace, String extPointName, Object... args);

    /**
     * 执行扩展点
     *
     * @param namespace       模型编码
     * @param extPointName    扩展点名称
     * @param defaultConsumer 默认的执行函数
     * @param args            入参
     * @return 结果
     */
    Object run(String namespace, String extPointName, java.util.function.Function<Object[], Object> defaultConsumer, Object... args);

}
