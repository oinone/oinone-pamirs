package pro.shushi.pamirs.trigger.service;

import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;

import java.util.Collection;
import java.util.List;

/**
 * 任务服务
 *
 * @author Adamancy Zhang
 * @date 2020-11-02 16:40
 */
public interface TaskActionService<T> {

    /**
     * 提交任务项
     *
     * @param taskItem 任务项
     * @return 成功返回true, 否则返回false
     */
    Boolean submit(T taskItem);

    /**
     * 查询任务项
     *
     * @return 任务列表
     */
    List<T> selectList(ScheduleQuery wrapper);

    /**
     * <h>通过实体参数查询待执行任务项数量</h>
     * <p>仅查询当前表</p>
     *
     * @param entity 任务查询参数
     * @return 待执行任务项数量
     */
    Long countByEntity(T entity);

    /**
     * <h>通过实体参数查询待执行任务项</h>
     * <p>仅查询当前表</p>
     *
     * @param entity 任务查询参数
     * @return 待执行任务项列表
     */
    List<T> selectListByEntity(T entity);

    /**
     * 删除任务项
     *
     * @param technicalName 技术名称
     * @return 删除结果
     */
    Boolean delete(String technicalName);

    /**
     * 删除任务项（批量）
     *
     * @param technicalNames 技术名称
     * @return 删除结果
     */
    Boolean deleteBatch(Collection<String> technicalNames);

    /**
     * 激活任务项
     *
     * @param technicalName 技术名称
     * @return 激活结果
     */
    Boolean active(String technicalName);

    /**
     * 激活任务项(批量)
     *
     * @param technicalNames 技术名称
     * @return 激活结果
     */
    Boolean activeBatch(Collection<String> technicalNames);

    /**
     * 取消任务项
     *
     * @param technicalName 技术名称
     * @return 取消结果
     */
    Boolean cancel(String technicalName);

    /**
     * 取消任务项(批量)
     *
     * @param technicalNames 技术名称
     * @return 取消结果
     */
    Boolean cancelBatch(Collection<String> technicalNames);
}
