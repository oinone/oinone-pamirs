package pro.shushi.pamirs.framework.faas.spi.api.remote;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 远程函数帮助类
 * <p>
 * 2021/1/26 9:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface IRemoteFunctionHelper {

    boolean isRemoteFunction(Function function);

    <R> R run(Function function, Object... args);

}
