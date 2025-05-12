package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

import java.util.List;

import static pro.shushi.pamirs.meta.constant.ExtPointConstants.AFTER_SUFFIX;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.deleteWithFieldBatch;

/**
 * 删除后置扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface DeleteAfterExtPoint<T> {

    @ExtPoint.name(deleteWithFieldBatch + AFTER_SUFFIX)
    @ExtPoint(displayName = "删除后置扩展点")
    default List<T> deleteAfter(List<T> data) {
        return data;
    }

}
