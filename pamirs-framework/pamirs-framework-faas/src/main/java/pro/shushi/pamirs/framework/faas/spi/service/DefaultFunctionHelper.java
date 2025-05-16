package pro.shushi.pamirs.framework.faas.spi.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.spi.api.remote.IRemoteFunctionHelper;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认函数帮助类
 * <p>
 * 2021/1/26 9:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
@Component
public class DefaultFunctionHelper implements IRemoteFunctionHelper {

    @Override
    public boolean isRemoteFunction(Function function) {
        return false;
    }

    @Override
    public <R> R run(Function function, Object... args) {
        throw new UnsupportedOperationException();
    }

}
