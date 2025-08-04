package pro.shushi.pamirs.auth.api.behavior;

import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import jakarta.annotation.Nullable;
import java.util.List;

/**
 * 权限项标准服务
 *
 * @author Adamancy Zhang at 19:16 on 2024-04-26
 */
public interface AuthPermissionStandardService<T extends AuthPermission> {

    /**
     * 创建
     *
     * @param data 数据
     * @return 结果
     */
    @Nullable
    T create(T data);

    /**
     * 批量创建
     *
     * @param list 数据列表
     * @return 结果
     */
    List<T> createBatch(List<T> list);

    /**
     * 更新
     *
     * @param data 数据
     * @return 结果
     */
    @Nullable
    T update(T data);

    /**
     * 批量更新
     *
     * @param list 数据列表
     * @return 影响行数
     */
    Integer updateBatch(List<T> list);

    /**
     * 条件更新
     *
     * @param data    需要更新的数据
     * @param wrapper 更新条件
     * @return 影响行数
     */
    Integer updateByWrapper(T data, LambdaUpdateWrapper<T> wrapper);

    /**
     * 创建或更新
     *
     * @param data 数据
     * @return 结果
     */
    @Nullable
    T createOrUpdate(T data);

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
     * 条件删除
     *
     * @param wrapper 删除条件
     * @return 影响行数
     */
    Integer deleteByWrapper(LambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询
     *
     * @param page         分页参数
     * @param queryWrapper 查询条件
     * @return 分页结果集
     */
    Pagination<T> queryPage(Pagination<T> page, LambdaQueryWrapper<T> queryWrapper);

    /**
     * 简单条件查询
     *
     * @param query 简单查询条件
     * @return 结果
     */
    @Nullable
    T queryOne(T query);

    /**
     * 条件查询
     *
     * @param queryWrapper 查询参数
     * @return 结果集
     */
    @Nullable
    T queryOneByWrapper(LambdaQueryWrapper<T> queryWrapper);

    /**
     * 条件查询
     *
     * @param queryWrapper 查询参数
     * @return 结果集
     */
    List<T> queryListByWrapper(LambdaQueryWrapper<T> queryWrapper);

    /**
     * 数量统计
     *
     * @param queryWrapper 查询参数
     * @return 数量
     */
    Long count(LambdaQueryWrapper<T> queryWrapper);
}
