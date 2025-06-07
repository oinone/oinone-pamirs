package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import java.util.List;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.CALLBACK;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.updateBatch;

/**
 * 批量更新回调扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface UpdateBatchCallbackExtPoint<T> {

    @ExtPoint.name(updateBatch + CALLBACK)
    @ExtPoint(displayName = "条件更新回调扩展点")
    default List<T> updateBatchCallback(List<T> data) {
        return data;
    }

}
