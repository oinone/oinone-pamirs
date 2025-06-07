package pro.shushi.pamirs.boot.rpc.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.spi.api.remote.IRemoteServiceInit;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;
import java.util.Map;

/**
 * 服务默认加载器
 * <p>
 * 2020/7/27 12:36 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
@Component
public class DefaultRemoteServiceInit implements IRemoteServiceInit {

    @Override
    public boolean init(Map<String, Meta> metaMap) {
        return Boolean.TRUE;
    }

    @Override
    public boolean init(List<String> models) {
        return Boolean.TRUE;
    }

}
