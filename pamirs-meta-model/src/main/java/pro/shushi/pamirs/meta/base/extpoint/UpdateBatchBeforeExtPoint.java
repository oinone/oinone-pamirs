package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;

import java.util.List;

/**
 * 批量更新前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
public interface UpdateBatchBeforeExtPoint<T> {

    @ExtPoint(name = "updateBatchBefore")
    List<T> updateBatchBefore(List<T> data);

}
