package pro.shushi.pamirs.meta.base.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.dto.crud.Condition;
import pro.shushi.pamirs.meta.api.dto.crud.Page;
import pro.shushi.pamirs.meta.api.dto.crud.PageCondition;
import pro.shushi.pamirs.meta.api.dto.crud.UpdateCondition;

import java.util.List;

/**
 * 默认读写扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Fun
public class DefaultReadWriteExtPoint<T> implements
        CountBeforeExtPoint<T>,
        CreateBeforeExtPoint<T>,
        CreateAfterExtPoint<T>,
        CreateBatchBeforeExtPoint<T>,
        CreateBatchAfterExtPoint<T>,
        DeleteBeforeExtPoint,
        PageAfterExtPoint<T>,
        PageBeforeExtPoint<T>,
        QueryListBeforeExtPoint<T>,
        QueryListAfterExtPoint<T>,
        QueryOneBeforeExtPoint<T>,
        QueryOneAfterExtPoint<T>,
        UpdateAfterExtPoint<T>,
        UpdateBeforeExtPoint<T>,
        UpdateBatchBeforeExtPoint<T>,
        UpdateBatchAfterExtPoint<T>,
        UpdateConditionBeforeExtPoint<T>,
        UpdateConditionAfterExtPoint<T>
{

    @Override
    @ExtPoint.Implement(displayName = "获取数量前置扩展点", priority = 999)
    public Condition<T> countBefore(Condition<T> condition) {
        return condition;
    }

    @Override
    @ExtPoint.Implement(displayName = "新增后置扩展点", priority = 999)
    public T createAfter(T data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "新增前置扩展点", priority = 999)
    public T createBefore(T data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "批量新增后置扩展点", priority = 999)
    public List<T> createBatchAfter(List<T> data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "批量新增前置扩展点", priority = 999)
    public List<T> createBatchBefore(List<T> data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "删除前置扩展点", priority = 999)
    public Condition deleteBefore(Condition data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "分页查询后置扩展点", priority = 999)
    public Page<T> queryPageAfter(Page<T> page) {
        return page;
    }

    @Override
    @ExtPoint.Implement(displayName = "分页查询前置扩展点", priority = 999)
    public PageCondition<T> queryPageBefore(PageCondition<T> data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "查询列表后置扩展点", priority = 999)
    public List<T> queryListAfter(List<T> data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "查询列表前置扩展点", priority = 999)
    public PageCondition<T> queryListBefore(PageCondition<T> condition) {
        return condition;
    }

    @Override
    @ExtPoint.Implement(displayName = "查询单条记录后置扩展点", priority = 999)
    public T queryOneAfter(T data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "查询单条记录前置扩展点", priority = 999)
    public T queryOneBefore(T data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "更新后置扩展点", priority = 999)
    public T updateAfter(T data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "批量更新前置扩展点", priority = 999)
    public List<T> updateBatchBefore(List<T> data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "批量更新后置扩展点", priority = 999)
    public List<T> updateBatchAfter(List<T> data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "更新前置扩展点", priority = 999)
    public T updateBefore(T data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "条件更新后置扩展点", priority = 999)
    public T updateConditionAfter(T data) {
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "条件更新前置扩展点", priority = 999)
    public UpdateCondition<T> updateConditionBefore(UpdateCondition<T> condition) {
        return condition;
    }
}
