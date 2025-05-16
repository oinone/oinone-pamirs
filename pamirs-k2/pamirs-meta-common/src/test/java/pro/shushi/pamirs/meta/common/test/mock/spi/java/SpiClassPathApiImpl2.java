package pro.shushi.pamirs.meta.common.test.mock.spi.java;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.AutoFill;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.path.SpiClassPathApi;

import java.util.List;

import static pro.shushi.pamirs.meta.common.constants.PackageConstants.PACKAGE_META_BASE;
import static pro.shushi.pamirs.meta.common.constants.PackageConstants.PACKAGE_PAMIRS;

/**
 * SPI类路径API实现
 * <p>
 * 2020/8/4 5:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(10)
@SPI.Service("p2")
public class SpiClassPathApiImpl2 implements SpiClassPathApi {

    @Override
    public List<String> path() {
        return Lists.newArrayList(PACKAGE_PAMIRS, PACKAGE_META_BASE);
    }

    public String value;

    @SuppressWarnings("unused")
    @AutoFill
    public void setValue(){
        value = "p2";
    }

}
