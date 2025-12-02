package pro.shushi.pamirs.business.api.spi;

import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.ux.common.entity.HoldSupplier;

import java.util.List;

/**
 * 获取当前员工API
 *
 * @author Adamancy Zhang at 17:35 on 2025-12-01
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CurrentEmployeeFetcher {

    /**
     * 获取当前用户绑定员工
     */
    PamirsEmployee fetch();

    /**
     * 获取当前用户所属部门下的员工列表
     */
    List<PamirsEmployee> fetchDeptEmployeeList();

    /**
     * 获取当前用户所属部门及下级部门的员工列表
     */
    List<PamirsEmployee> fetchDeptWithChildrenEmployeeList();

    HoldSupplier<CurrentEmployeeFetcher> holder = HoldSupplier.getDefaultExtension(CurrentEmployeeFetcher.class);

    static CurrentEmployeeFetcher get() {
        return holder.get();
    }
}
