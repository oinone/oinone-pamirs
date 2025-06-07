package pro.shushi.pamirs.trigger.service;


import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.common.Page;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.util.Collection;
import java.util.List;

/**
 * @author Adamancy Zhang at 15:02 on 2021-04-27
 */
@Fun(ScheduleOperateService.FUN_NAMESPACE)
public interface ScheduleOperateService {

    String FUN_NAMESPACE = "trigger.ScheduleOperateService";

    /**
     * 插入任务项
     *
     * @param task {@link ScheduleItem}
     * @return 影响行数
     */
    @Function
    Long addScheduleTask(ScheduleItem task);


    /**
     * 插入任务项执行记录
     *
     * @param task {@link ScheduleItem}
     * @return 影响行数
     */
    @Function
    Long addScheduleTaskRecord(ScheduleItem task);

    /**
     * 通过技术名称创建或更新任务项
     *
     * @param task 任务 {@link ScheduleItem}
     * @return 影响行数
     */
    @Function
    Integer createOrUpdateScheduleTaskByTechnicalName(ScheduleItem task);

    /**
     * 通过技术名称删除任务项
     *
     * @param technicalName {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    @Function
    Integer deleteScheduleTaskByTechnicalName(String technicalName);

    /**
     * 通过技术名称批量删除任务项
     *
     * @param technicalNames {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    @Function
    Integer deleteScheduleTaskByTechnicalNameBatch(Collection<String> technicalNames);

    /**
     * 通过技术名称取消任务项
     *
     * @param technicalName {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    @Function
    Integer activeScheduleTaskByTechnicalName(String technicalName);

    /**
     * 通过技术名称批量取消任务项
     *
     * @param technicalNames {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    @Function
    Integer activeScheduleTaskByTechnicalNameBatch(Collection<String> technicalNames);

    /**
     * 通过技术名称取消任务项
     *
     * @param technicalName {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    @Function
    Integer cancelScheduleTaskByTechnicalName(String technicalName);

    /**
     * 通过技术名称批量取消任务项
     *
     * @param technicalNames {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    @Function
    Integer cancelScheduleTaskByTechnicalNameBatch(Collection<String> technicalNames);

    /**
     * 取消执行schedule任务
     *
     * @param bizId 任务bizId
     */
    @Function
    void cancelScheduleTaskByBizId(Long bizId);

    /**
     * 根据实体参数查询待执行任务数量
     *
     * @param task schedule任务
     * @return 查询结果
     */
    @Function
    Long countByEntity(ScheduleItem task);

    /**
     * 根据实体参数查询待执行任务列表
     *
     * @param task schedule任务
     * @return 查询结果
     */
    @Function
    List<ScheduleItem> selectListByEntity(ScheduleItem task);

    /**
     * 根据条件查询任务列表
     *
     * @param where       条件
     * @param order       排序
     * @param currentPage 当前页数
     * @param size        每页数量
     * @return 任务列表
     */
    @Function
    Page<ScheduleItem> selectListByWhere(String where, String order, Integer currentPage, Integer size);

    /**
     * 根据id查询
     *
     * @param id Long
     * @return ScheduleItem
     */
    @Function
    ScheduleItem selectById(Long id);

    /**
     * 按bizId查询，用于bizId去重的场景
     *
     * @param bizId 任务bizId
     * @return int
     */
    @Function
    List<ScheduleItem> selectByBizId(Long bizId);

    /**
     * 通过id更新任务
     *
     * @param task 任务
     * @return 影响行数
     */
    @Function
    Integer updateById(ScheduleItem task);

    /**
     * 根据id删除任务
     *
     * @param ids 主键列表
     * @return 影响行数
     */
    @Function
    Integer deleteByIds(List<Long> ids);

    /**
     * 根据id取消任务
     *
     * @param ids 主键列表
     * @return 影响行数
     */
    @Function
    Integer cancelByIds(List<Long> ids);

    /**
     * 根据id恢复任务
     *
     * @param ids 主键列表
     * @return 影响行数
     */
    @Function
    Integer recoveryByIds(List<Long> ids);
}
