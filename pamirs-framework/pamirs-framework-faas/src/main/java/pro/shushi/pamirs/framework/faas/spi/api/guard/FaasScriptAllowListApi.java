package pro.shushi.pamirs.framework.faas.spi.api.guard;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 支持表达式调用的函数白名单与黑名单SPI
 * 2021/3/4 11:14 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FaasScriptAllowListApi {

    default Set<String> classWhiteList() {
        return null;
    }

    default Set<String> classBlackList() {
        return null;
    }

    default Set<String> namespaceWhiteList() {
        return null;
    }

    default Set<String> namespaceBlackList() {
        return null;
    }

    default Set<String> funSignWhiteList() {
        return null;
    }

    default Set<String> funSignBlackList() {
        return null;
    }

}
