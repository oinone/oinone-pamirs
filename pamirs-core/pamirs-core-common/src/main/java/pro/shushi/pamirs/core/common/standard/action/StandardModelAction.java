package pro.shushi.pamirs.core.common.standard.action;

import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.BaseModel;

import jakarta.annotation.Nullable;
import java.util.List;

/**
 * 标准模型动作
 *
 * @author Adamancy Zhang on 2021-05-18 11:20
 */
public interface StandardModelAction<T extends BaseModel> {

    /**
     * 实例构造
     *
     * @param data 数据
     * @return 结果
     */
    T construct(T data);

    /**
     * 下拉触发
     *
     * @param data 数据
     * @return 结果
     */
    T constructMirror(T data);

    /**
     * 创建
     *
     * @param data 数据
     * @return 结果
     */
    @Nullable
    T create(T data);

    /**
     * 更新
     *
     * @param data 数据
     * @return 结果
     */
    @Nullable
    T update(T data);

    /**
     * 批量删除
     *
     * @param list 数据列表
     * @return 结果
     */
    List<T> delete(List<T> list);

    /**
     * 删除
     *
     * @param data 数据
     * @return 结果
     */
    @Nullable
    T deleteOne(T data);

    /**
     * 分页查询
     *
     * @param page         分页参数
     * @param queryWrapper 查询条件
     * @return 分页结果集
     */
    Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper);

    /**
     * 简单条件查询
     *
     * @param query 简单查询条件
     * @return 结果
     */
    @Nullable
    T queryOne(T query);
}
