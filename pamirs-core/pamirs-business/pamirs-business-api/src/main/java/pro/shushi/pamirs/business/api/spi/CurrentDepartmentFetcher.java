package pro.shushi.pamirs.business.api.spi;

import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.ux.common.entity.HoldSupplier;

import java.util.List;

/**
 * 获取当前部门API
 *
 * @author Adamancy Zhang at 17:57 on 2025-12-01
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CurrentDepartmentFetcher {

    /**
     * 获取当前用户所属部门
     *
     * @return 当前用户所属主部门
     */
    PamirsDepartment fetch();

    /**
     * 获取当前用户所属部门
     *
     * @return 当前用户所属部门
     */
    List<PamirsDepartment> fetchList();

    /**
     * 获取当前用户所属部门及子部门
     *
     * @return 当前用户所属部门及子部门
     */
    List<PamirsDepartment> fetchListWithChildren();

    /**
     * 填充指定部门子节点
     *
     * @param departments 指定部门列表
     * @return 包含指定部门及子部门的完整列表
     */
    List<PamirsDepartment> fillDepartmentChildren(List<PamirsDepartment> departments);

    HoldSupplier<CurrentDepartmentFetcher> holder = HoldSupplier.getDefaultExtension(CurrentDepartmentFetcher.class);

    static CurrentDepartmentFetcher get() {
        return holder.get();
    }
}
