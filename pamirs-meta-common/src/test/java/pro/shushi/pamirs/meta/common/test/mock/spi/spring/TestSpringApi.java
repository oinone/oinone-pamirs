package pro.shushi.pamirs.meta.common.test.mock.spi.spring;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * SPI类路径API
 * <p>
 * 2020/8/3 1:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(value = "p1", factory = SpringServiceLoaderFactory.class)
public interface TestSpringApi {

    List<String> path();

}
