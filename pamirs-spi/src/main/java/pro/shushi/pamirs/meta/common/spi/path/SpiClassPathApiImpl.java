package pro.shushi.pamirs.meta.common.spi.path;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;

import java.util.List;

import static pro.shushi.pamirs.meta.common.constants.SpiNamespaceConstants.PACKAGE_PAMIRS;

/**
 * SPI类路径API实现
 * <p>
 * 2020/8/4 5:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
public class SpiClassPathApiImpl implements SpiClassPathApi {

    @Override
    public List<String> path() {
        return Lists.newArrayList(PACKAGE_PAMIRS);
    }

}
