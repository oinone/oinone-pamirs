package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;

/**
 * 默认读写扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
@XService(publish = false)
public interface ReadWriteExtPoint<T> extends
        CreateBeforeExtPoint<T>,
        CreateAfterExtPoint<T>,
        UpdateBeforeExtPoint<T>,
        UpdateAfterExtPoint<T>,
        DeleteBeforeExtPoint<T>,
        DeleteAfterExtPoint<T>,

        CreateBatchBeforeExtPoint<T>,
        CreateBatchAfterExtPoint<T>,
        UpdateBatchBeforeExtPoint<T>,
        UpdateBatchCallbackExtPoint<T>,
        UpdateBatchAfterExtPoint<T>,

        QueryByPkBeforeExtPoint<T>,
        QueryByPkAfterExtPoint<T>,
        QueryOneBeforeExtPoint<T>,
        QueryOneAfterExtPoint<T>,
        QueryListBeforeExtPoint<T>,
        QueryListAfterExtPoint<T>,
        PageBeforeExtPoint<T>,
        PageAfterExtPoint<T>,
        CountBeforeExtPoint<T>,

        UpdateConditionBeforeExtPoint<T>,
        UpdateConditionCallbackExtPoint<T>,
        UpdateConditionAfterExtPoint<T> {

}
