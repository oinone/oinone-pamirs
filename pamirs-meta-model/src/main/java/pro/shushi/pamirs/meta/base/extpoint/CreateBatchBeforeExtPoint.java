package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import java.util.List;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.BEFORE_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.createBatch;

/**
 * 批量新增前置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface CreateBatchBeforeExtPoint<T> {

    @ExtPoint.name(createBatch + BEFORE_SUFFIX)
    @ExtPoint(displayName = "批量新增前置扩展点")
    default List<T> createBatchBefore(List<T> data) {
        return data;
    }

}
