package pro.shushi.pamirs.framework.faas.spi.api.fun;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 内置商业函数接口扩展点
 * 2021/3/3 10:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BusinessFunctionsApi {

    /**
     * 获取当前用户的公司id
     *
     * @return 公司id
     */
    Long currentCorpId();

    /**
     * 获取当前用户的公司
     *
     * @return 公司
     */
    Object currentCorp();

    /**
     * 获取当前用户的店铺id
     *
     * @return 店铺id
     */
    Long currentShopId();

    /**
     * 获取当前用户的店铺id
     *
     * @return 店铺id
     */
    Object currentShop();

}
