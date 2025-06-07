package pro.shushi.pamirs.boot.common.spi.api.boot;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 启动预加载模型接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BootModelPrepareApi {

    default void prepare(Map<String/*model*/, String/*simulate model*/> modelMap) {

    }

}
