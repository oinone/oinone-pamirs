package pro.shushi.pamirs.framework.faas.spi.api.fun;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 内置上下文函数接口扩展点 - 用户相关
 * 2021/3/3 10:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ContextFunctionsUserApi {

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    Object currentUser();

}
