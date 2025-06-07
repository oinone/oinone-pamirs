package pro.shushi.pamirs.middleware.schedule.core.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.shushi.pamirs.middleware.schedule.core.dao.sharding.TableSeg;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleAllQuery;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;

import java.util.Collection;
import java.util.List;

/**
 * ITaskItemEntityMapper
 *
 * @author yakir on 2020/07/09 12:16.
 */
@Mapper
@TableSeg(tableName = "pamirs_schedule", shardBy = "tableNum")
public interface ScheduleItemMapper {

    /**
     * 添加任务
     *
     * @param task 任务
     * @return 影响行数
     */
    int insert(ScheduleItem task);

    /**
     * 添加任务记录
     *
     * @param task 任务
     * @return 影响行数
     */
    int insertRecord(ScheduleItem task);

    /**
     * 通过业务Id更新任务状态
     *
     * @param task 任务
     * @return 影响行数
     */
    int updateTaskStatusById(ScheduleItem task);

    /**
     * 通过业务Id更新任务状态
     *
     * @param wrapper 查询参数
     * @return 影响行数
     */
    int updateTaskStatusByBizId(ScheduleItem wrapper);

    /**
     * 通过业务编码更新任务状态
     *
     * @param wrapper 查询参数
     * @return 影响行数
     */
    int updateTaskStatusByBizCode(ScheduleItem wrapper);

    /**
     * 根据任务Id查询任务
     *
     * @param wrapper 查询参数
     * @return 任务
     */
    ScheduleItem selectById(ScheduleItem wrapper);

    /**
     * 根据查询参数获取任务列表
     *
     * @param wrapper 查询参数
     * @return 任务列表
     */
    List<ScheduleItem> selectList(ScheduleQuery wrapper);

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
     * 根据技术名称创建或更新任务列表
     *
     * @param task 任务
     * @return 任务列表
     */
    int createScheduleItem(ScheduleItem task);

    /**
     * 根据技术名称更新任务列表
     *
     * @param task 任务
     * @return 任务列表
     */
    int updateByTechnicalName(ScheduleItem task);


    /**
     * 根据查询参数获取需要迁移的任务列表
     *
     * @param wrapper 查询参数
     * @return 任务列表
     */
    List<ScheduleItem> selectDelayList(ScheduleQuery wrapper);

    /**
     * 根据技术名称批量删除任务
     *
     * @param environment       环境参数
     * @param technicalNameList 技术名称列表
     * @return 影响行数
     */
    int deleteTaskByTechnicalNameList(@Param("environment") ScheduleEnvironment environment, @Param("collection") Collection<String> technicalNameList, @Param("now") Long now);

    /**
     * 根据技术名称批量激活任务
     *
     * @param environment    环境参数
     * @param technicalNames 技术名称列表
     * @return 影响行数
     */
    int activeTaskByTechnicalNameList(@Param("environment") ScheduleEnvironment environment, @Param("collection") Collection<String> technicalNames);

    /**
     * 根据技术名称批量取消任务
     *
     * @param technicalNameList 技术名称列表
     * @return 影响行数
     */
    int cancelTaskByTechnicalNameList(@Param("environment") ScheduleEnvironment environment, @Param("collection") Collection<String> technicalNameList);

    /**
     * 根据条件查询任务数量
     *
     * @param environment 环境参数
     * @return
     */
    int countByWhere(@Param("environment") ScheduleEnvironment environment, @Param("where") String where);

    /**
     * 根据条件查询任务列表
     *
     * @param environment 环境参数
     * @param where       条件
     * @param order       排序
     * @return 任务列表
     */
    List<ScheduleItem> selectListByWhere(@Param("environment") ScheduleEnvironment environment, @Param("where") String where, @Param("order") String order,
                                         @Param("offset") Integer offset, @Param("count") Integer count);

    /**
     * 通过主键更新（全部可更新字段）
     *
     * @param task 任务
     * @return 影响行数
     */
    int updateByPrimaryKey(ScheduleItem task);

    /**
     * 通过主键删除任务（逻辑删除）
     *
     * @param environment 环境参数
     * @param ids         主键列表
     * @return 影响行数
     */
    int deleteByIds(@Param("environment") ScheduleEnvironment environment, @Param("list") List<Long> ids, @Param("now") Long now);

    /**
     * 通过主键取消任务
     *
     * @param environment 环境参数
     * @param ids         主键列表
     * @return 影响行数
     */
    int cancelByIds(@Param("environment") ScheduleEnvironment environment, @Param("list") List<Long> ids);

    /**
     * 通过主键恢复任务
     *
     * @param environment 环境参数
     * @param ids         主键列表
     * @return 影响行数
     */
    int recoveryByIds(@Param("environment") ScheduleEnvironment environment, @Param("list") List<Long> ids);

    int insertSelective(ScheduleItem record);

    /**
     * 根据任务id列表修改下次执行时间
     */
    int updateNextRetryTimeByIds(ScheduleItem record);

    int updateRetryNum(ScheduleItem record);

    /**
     * 资产串行执行的List查询
     */
    List<ScheduleItem> selectListForSerial(ScheduleQuery query);

    /**
     * 资产串行执行的List查询
     */
    List<ScheduleItem> selectTypesListForSerial(ScheduleQuery query);

    List<ScheduleItem> selectListForParam(@Param("taskType") String taskType, @Param("param") String param, @Param("tableNum") Integer tableNum);

    List<ScheduleItem> selectScheduleList(ScheduleQuery query);

    List<ScheduleItem> mSelectList(ScheduleQuery query);

    Integer mCountList(ScheduleQuery query);


    List<ScheduleItem> mselectDelayList(ScheduleQuery query);

    int update2SucessByPrimaryKey(ScheduleItem record);

    int update2TransferByPrimaryKey(ScheduleItem record);

    /**
     * 批量修改迁移任务状态
     */
    int update2TransferByIds(ScheduleItem record);

    int update2RetryByPrimaryKey(ScheduleItem record);

    /**
     * 循环任务
     */
    int updateSuccCycleByPrimaryKey(ScheduleItem record);

    /**
     * 循环任务
     */
    int updateRetryCycleByPrimaryKey(ScheduleItem record);

    List<ScheduleItem> selectByBizId(@Param("environment") ScheduleEnvironment environment, @Param("bizId") Long bizId);

    /**
     * 根据bizid更新param
     */
    int updateParamById(ScheduleItem record);

    int getChildTaskCount(ScheduleItem record);

    int updateSuccTask(ScheduleItem record);

    int update2CancelByParam(ScheduleItem record);

    List<ScheduleItem> selectListForError(ScheduleAllQuery query);

    int selectCountForError(ScheduleAllQuery query);
}

