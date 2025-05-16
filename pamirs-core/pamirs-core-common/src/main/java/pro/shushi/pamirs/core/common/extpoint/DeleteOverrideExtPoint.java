package pro.shushi.pamirs.core.common.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import java.util.List;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.OVERRIDE;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.deleteWithFieldBatch;

/**
 * 删除覆盖扩展点
 *
 * @author Adamancy Zhang
 * @date 2020-11-30 22:51
 */
@Fun
@XService(publish = false)
public interface DeleteOverrideExtPoint<T> {

    /**
     * 覆盖处理
     *
     * @param list 数据列表
     * @return 数据列表
     */
    @ExtPoint.name(deleteWithFieldBatch + OVERRIDE)
    @ExtPoint(displayName = "删除覆盖扩展点")
    List<T> deleteOverride(List<T> list);
}
