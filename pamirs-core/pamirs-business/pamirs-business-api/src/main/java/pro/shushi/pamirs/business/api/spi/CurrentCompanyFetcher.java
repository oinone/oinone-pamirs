package pro.shushi.pamirs.business.api.spi;

import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.ux.common.entity.HoldSupplier;

import java.util.List;

/**
 * 获取当前公司API
 *
 * @author Adamancy Zhang at 12:30 on 2025-12-02
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CurrentCompanyFetcher {

    /**
     * 获取当前用户所属公司
     *
     * @return 当前用户所属主公司
     */
    PamirsCompany fetch();

    /**
     * 获取当前用户所属公司
     *
     * @return 当前用户所属公司
     */
    List<PamirsCompany> fetchList();

    HoldSupplier<CurrentCompanyFetcher> holder = HoldSupplier.getDefaultExtension(CurrentCompanyFetcher.class);

    static CurrentCompanyFetcher get() {
        return holder.get();
    }
}
