package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 首页获取服务
 *
 * @author Adamancy Zhang at 18:11 on 2023-12-21
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface HomepageFetcherApi {

    /**
     * 获取全局首页
     *
     * @return 全局首页
     */
    UeModule fetchGlobalHomepage();

    /**
     * 获取应用首页
     *
     * @param module 模块
     * @return 模块首页
     */
    Action fetchApplicationHomePage(UeModule module, String homepageModel, String homepageName);
}
