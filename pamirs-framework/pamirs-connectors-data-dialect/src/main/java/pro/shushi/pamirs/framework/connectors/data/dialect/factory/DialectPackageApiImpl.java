package pro.shushi.pamirs.framework.connectors.data.dialect.factory;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DialectPackageApi;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 方言组件接口包列表
 * 2020/8/8 11:14 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
@Component
public class DialectPackageApiImpl implements DialectPackageApi {

    @Override
    public List<String> packages() {
        return Lists.newArrayList(PackageConstants.PACKAGE_DATA);
    }

}
