package pro.shushi.pamirs.framework.session.tenant.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * tenant session构造器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionTenantApi {

    /**
     * 获取租户（运行时）
     *
     * @return 租户
     */
    default String getTenant() {
        return null;
    }

    /**
     * 设置租户
     *
     * @param tenant 租户
     */
    void setTenant(String tenant);

}
