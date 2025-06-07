package pro.shushi.pamirs.middleware.schedule.api;


import pro.shushi.pamirs.middleware.schedule.common.Page;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.util.Collection;
import java.util.List;

public interface ScheduleService {

    /**
     * 插入任务项
     *
     * @param task {@link ScheduleItem}
     * @return 影响行数
     */
    long addScheduleTask(ScheduleItem task);


    /**
     * 插入任务项执行记录
     *
     * @param task {@link ScheduleItem}
     * @return 影响行数
     */
    long addScheduleTaskRecord(ScheduleItem task);

    /**
     * 通过技术名称创建或更新任务项
     *
     * @param task 任务 {@link ScheduleItem}
     * @return 影响行数
     */
    int createOrUpdateScheduleTaskByTechnicalName(ScheduleItem task);

    /**
     * 通过技术名称删除任务项
     *
     * @param environment   {@link ScheduleEnvironment}
     * @param technicalName {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    int deleteScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName);

    /**
     * 通过技术名称批量删除任务项
     *
     * @param environment    {@link ScheduleEnvironment}
     * @param technicalNames {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    int deleteScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames);

    /**
     * 通过技术名称取消任务项
     *
     * @param environment   {@link ScheduleEnvironment}
     * @param technicalName {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    int activeScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName);

    /**
     * 通过技术名称批量取消任务项
     *
     * @param environment    {@link ScheduleEnvironment}
     * @param technicalNames {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    int activeScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames);

    /**
     * 通过技术名称取消任务项
     *
     * @param environment   {@link ScheduleEnvironment}
     * @param technicalName {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    int cancelScheduleTaskByTechnicalName(ScheduleEnvironment environment, String technicalName);

    /**
     * 通过技术名称批量取消任务项
     *
     * @param environment    {@link ScheduleEnvironment}
     * @param technicalNames {@link ScheduleItem#getTechnicalName()}
     * @return 影响行数
     */
    int cancelScheduleTaskByTechnicalNameBatch(ScheduleEnvironment environment, Collection<String> technicalNames);

    /**
     * 取消执行schedule任务
     *
     * @param environment {@link ScheduleEnvironment}
     * @param bizId       任务bizId
     */
    void cancelScheduleTaskByBizId(ScheduleEnvironment environment, Long bizId);

    /**
     * 根据实体参数查询待执行任务数量
     *
     * @param task schedule任务
     * @return 查询结果
     */
    Long countByEntity(ScheduleItem task);

    /**
     * 根据实体参数查询待执行任务列表
     *
     * @param task schedule任务
     * @return 查询结果
     */
    List<ScheduleItem> selectListByEntity(ScheduleItem task);

    /**
     * 根据条件查询任务列表
     *
     * @param environment {@link ScheduleEnvironment}
     * @param where       条件
     * @param order       排序
     * @param currentPage 当前页数
     * @param size        每页数量
     * @return 任务列表
     */
    Page<ScheduleItem> selectListByWhere(ScheduleEnvironment environment, String where, String order, Integer currentPage, Integer size);

    /**
     * 根据id查询
     *
     * @param environment {@link ScheduleEnvironment}
     * @param id          Long
     * @return ScheduleItem
     */
    ScheduleItem selectById(ScheduleEnvironment environment, Long id);

    /**
     * 按bizId查询，用于bizId去重的场景
     *
     * @param environment {@link ScheduleEnvironment}
     * @param bizId       任务bizId
     * @return int
     */
    List<ScheduleItem> selectByBizId(ScheduleEnvironment environment, Long bizId);

    /**
     * 通过id更新任务
     *
     * @param task 任务
     * @return 影响行数
     */
    int updateById(ScheduleItem task);

    /**
     * 根据id删除任务
     *
     * @param environment {@link ScheduleEnvironment}
     * @param ids         主键列表
     * @return 影响行数
     */
    int deleteByIds(ScheduleEnvironment environment, List<Long> ids);

    /**
     * 根据id取消任务
     *
     * @param environment {@link ScheduleEnvironment}
     * @param ids         主键列表
     * @return 影响行数
     */
    int cancelByIds(ScheduleEnvironment environment, List<Long> ids);

    /**
     * 根据id恢复任务
     *
     * @param environment {@link ScheduleEnvironment}
     * @param ids         主键列表
     * @return 影响行数
     */
    int recoveryByIds(ScheduleEnvironment environment, List<Long> ids);

//    /**
//     * 查找任务表数据
//     *
//     * @param taskType    任务类型
//     * @param bizId       任务bizId
//     * @param application 任务应用名称
//     * @return <pre>List<ScheduleItem></pre>
//     */
//    List<ScheduleItem> selectScheduleItem(String taskType, Long bizId, String application);

//    /**
//     * 根据param查询任务
//     *
//     * @param taskType String
//     * @param param    String
//     * @return <pre>List<ScheduleItem></pre>
//     */
//    List<ScheduleItem> selectTasksByParam(String taskType, String param);

//    /**
//     * 管理后台查询普通任务list
//     *
//     * @param scheduleQuery ScheduleQuery
//     * @return <pre>Page<ScheduleItem></pre>
//     */
//    Page<ScheduleItem> mSelectScheduleList(ScheduleQuery scheduleQuery);

//    /**
//     * 修改任务表数据
//     *
//     * @param ids           任务id列表
//     * @param nextRetryTime 下次任务执行时间
//     * @return int
//     */
//    int updateNextRetryTime(List<Long> ids, Long nextRetryTime);

//    /**
//     * 修改任务重试次数
//     *
//     * @param ids      List<Long>
//     * @param tableNum Integer
//     * @param retryNum Integer
//     * @return int
//     */
//    int updateRetryNum(List<Long> ids, Integer tableNum, Integer retryNum);

//    /**
//     * 批量删除任务
//     *
//     * @param ids    任务id列表
//     * @param remark 备注信息
//     * @return int
//     */
//    int deleteScheduleTask(List<Long> ids, String remark);
//
//    /**
//     * 批量删除任务
//     *
//     * @param ids      List<Long>
//     * @param tableNum Integer
//     * @param remark   String
//     * @return int
//     */
//    int deleteScheduleTask(List<Long> ids, Integer tableNum, String remark);

//    /**
//     * 更新任务参数
//     *
//     * @param bizId bizId
//     */
//    int updateScheduleTaskParam(Long bizId, String param);
//
//    /**
//     * 根据id更新任务状态
//     *
//     * @param id        Long
//     * @param bizStatus Integer
//     * @return int
//     */
//    int updateScheduleTaskStatusById(Long id, Integer bizStatus);
//
//    /**
//     * 根据parenttaskid和bizstatus查询子任务总数
//     *
//     * @param parentTaskId String
//     * @param bizStatus    Inter
//     * @return int
//     */
//    int getChildTaskCount(String parentTaskId, Integer bizStatus);
//
//    /**
//     * 任务成功完成更新状态
//     *
//     * @param bizId bizId
//     */
//    int updateSuccTask(Long bizId);
}
