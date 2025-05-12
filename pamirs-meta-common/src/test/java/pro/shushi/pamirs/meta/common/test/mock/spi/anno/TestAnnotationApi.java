package pro.shushi.pamirs.meta.common.test.mock.spi.anno;

import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * SPI类路径API
 * <p>
 * 2020/8/3 1:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI("p1")
public interface TestAnnotationApi {

    List<String> path();

}
