package pro.shushi.pamirs.framework.connectors.data.mapper;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.*;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants;
import pro.shushi.pamirs.framework.connectors.data.holder.PamirsBatchApiHolder;
import pro.shushi.pamirs.framework.connectors.data.mapper.aspect.ModelFill;
import pro.shushi.pamirs.framework.connectors.data.mapper.provider.PamirsMapperProvider;
import pro.shushi.pamirs.framework.connectors.data.util.MapperUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.enmu.BatchOpTypeEnum;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Pamirs 通用mapper
 * <p>
 * 2020-01-09 00:22
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Validated
public interface PamirsMapper<T> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    @ModelFill
    @Options(useGeneratedKeys = true, keyProperty = "*")
    @InsertProvider(type = PamirsMapperProvider.class)
    int insert(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 批量插入，一次提交foreach插入
     *
     * @param entityList 实体对象列表
     */
    @ModelFill
    @Options(useGeneratedKeys = true, keyProperty = "*")
    @InsertProvider(type = PamirsMapperProvider.class)
    int insertBatchForeach(@NotNull @Param(Constants.COLLECTION) List<T> entityList);

    /**
     * 批量插入，一次提交foreach插入
     *
     * @param entityList 实体对象列表
     * @param batchSize  批次数量
     * @return 影响行数
     */
    @ModelFill
    default int insertBatchForeachWithSize(@NotNull List<T> entityList, int batchSize) {
        return MapperUtils.batchFunction(entityList, this::insertBatchForeach, batchSize);
    }

    /**
     * 批量插入，一次提交批量提交数据
     *
     * @param entityList 实体对象列表
     */
    @SuppressWarnings("unchecked")
    @ModelFill
    default int insertBatchCommit(@NotNull List<T> entityList) {
        return PamirsBatchApiHolder.get().batchCommit(BatchOpTypeEnum.insert, entityList, PamirsMapper::insert);
    }

    /**
     * 批量插入，一次提交批量提交数据
     *
     * @param entityList 实体对象列表
     * @param batchSize  批次数量
     * @return 影响行数
     */
    @ModelFill
    default int insertBatchCommitWithSize(@NotNull List<T> entityList, int batchSize) {
        return MapperUtils.batchFunction(entityList, this::insertBatchCommit, batchSize);
    }

    /**
     * 批量插入，根据提交策略提交数据
     *
     * @param entityList 实体对象列表
     */
    @ModelFill
    default int insertBatch(@NotNull List<T> entityList) {
        return insertBatchWithSize(entityList, -1);
    }

    /**
     * 批量插入，根据提交策略提交数据
     *
     * @param entityList 实体对象列表
     * @param batchSize  批次数量
     * @return 影响行数
     */
    @ModelFill
    default int insertBatchWithSize(@NotNull List<T> entityList, int batchSize) {
        String model = Models.api().getModel(entityList);
        return MapperUtils.batchFunction(entityList,
                dataList -> Models.batch().strategy(BatchOpTypeEnum.insert,
                        model, batchSize,
                        computeBatchSize -> insertBatchForeachWithSize(dataList, computeBatchSize),
                        computeBatchSize -> insertBatchCommitWithSize(dataList, computeBatchSize)
                ), batchSize);
    }

    /**
     * 更新或插入一条记录
     * <br/>不支持乐观锁
     *
     * @param entity 实体对象，使用唯一索引来判断更新还是插入
     */
    @ModelFill
    @Options(useGeneratedKeys = true, keyProperty = "*")
    @InsertProvider(type = PamirsMapperProvider.class)
    int insertOrUpdate(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 批量更新或插入
     * <br/>如果entityList中有唯一索引重复数据，会抛出索引冲突异常
     *
     * @param entityList 实体对象，使用主键或唯一索引来判断更新还是插入
     */
    @ModelFill
    default int insertOrUpdateBatch(@NotNull @Param(Constants.COLLECTION) List<T> entityList) {
        return insertOrUpdateBatchWithSize(entityList, -1);
    }

    /**
     * 批量更新或插入，可以设置每批次的数量
     * <br/>如果entityList中有唯一索引重复数据，会抛出索引冲突异常
     *
     * @param entityList 实体对象，使用唯一索引来判断更新还是插入
     * @param batchSize  每批次的数量
     * @return 影响行数
     */
    @ModelFill
    default int insertOrUpdateBatchWithSize(@NotNull List<T> entityList, int batchSize) {
        return MapperUtils.batchFunction(entityList,
                dataList -> MapperUtils.insertOrUpdateBatch(dataList,
                        this::selectListByPks,
                        this::selectListByUniqueKey,
                        this::insertBatch,
                        this::updateBatchByUniqueKey
                ), batchSize);
    }

    /**
     * 批量更新或插入
     * <br/>
     * 底层方法，不建议使用
     * <br/>
     * 返回结果只有第一条记录会返回主键值
     * <br/>
     * 不支持乐观锁
     *
     * @param entityList 实体对象，使用唯一索引来判断更新还是插入
     */
    @Deprecated
    @ModelFill
    @Options(useGeneratedKeys = true, keyProperty = "*")
    @InsertProvider(type = PamirsMapperProvider.class)
    int insertOrUpdateBatchOnDuplicateKey(@NotNull @Param(Constants.COLLECTION) List<T> entityList);

    /**
     * 批量更新或插入，可以设置每批次的数量
     * <br/>
     * 底层方法，不建议使用
     * <br/>
     * 返回结果只有每一批第一条记录会返回主键值；如果希望所有记录都返回主键值，则将batchSize设置为1
     * <br/>
     * 不支持乐观锁
     *
     * @param entityList 实体对象，使用唯一索引来判断更新还是插入
     * @param batchSize  每批次的数量
     * @return 影响行数
     */
    @Deprecated
    @ModelFill
    default int insertOrUpdateBatchOnDuplicateKeyWithSize(@NotNull List<T> entityList, int batchSize) {
        return MapperUtils.batchFunction(entityList, this::insertOrUpdateBatchOnDuplicateKey, batchSize);
    }

    /**
     * 根据唯一索引批量更新，一次提交foreach更新
     *
     * @param entityList 实体对象，使用唯一索引来作为更新条件
     */
    @ModelFill
    @UpdateProvider(type = PamirsMapperProvider.class)
    int updateBatchForeachByUniqueKey(@NotNull @Param(Constants.COLLECTION) List<T> entityList);

    /**
     * 根据唯一索引批量更新，一次提交foreach更新
     *
     * @param entityList 实体对象列表
     * @param batchSize  批次数量
     * @return 影响行数
     */
    @ModelFill
    default int updateBatchForeachWithSize(@NotNull List<T> entityList, int batchSize) {
        return MapperUtils.batchFunction(entityList, this::updateBatchForeachByUniqueKey, batchSize);
    }

    /**
     * 根据唯一索引批量更新，一次提交批量提交数据
     *
     * @param entityList 实体对象列表
     */
    @SuppressWarnings("unchecked")
    @ModelFill
    default int updateBatchCommitByUniqueKey(@NotNull List<T> entityList) {
        return PamirsBatchApiHolder.get().batchCommit(BatchOpTypeEnum.update, entityList, PamirsMapper::updateByUniqueKey);
    }

    /**
     * 根据唯一索引批量更新，一次提交批量提交数据
     *
     * @param entityList 实体对象列表
     * @param batchSize  批次数量
     * @return 影响行数
     */
    @ModelFill
    default int updateBatchCommitWithSize(@NotNull List<T> entityList, int batchSize) {
        return MapperUtils.batchFunction(entityList, this::updateBatchCommitByUniqueKey, batchSize);
    }

    /**
     * 根据唯一索引批量更新，根据提交策略提交数据
     *
     * @param entityList 实体对象，使用唯一索引来作为更新条件
     */
    @ModelFill
    default int updateBatchByUniqueKey(@NotNull List<T> entityList) {
        return updateBatchWithSize(entityList, -1);
    }

    /**
     * 根据唯一索引批量更新，根据提交策略提交数据
     *
     * @param entityList 实体对象列表
     * @param batchSize  批次数量
     * @return 影响行数
     */
    @ModelFill
    default int updateBatchWithSize(@NotNull List<T> entityList, int batchSize) {
        String model = Models.api().getModel(entityList);
        return MapperUtils.batchFunction(entityList,
                dataList -> Models.batch().strategy(BatchOpTypeEnum.update,
                        model, batchSize,
                        computeBatchSize -> updateBatchForeachWithSize(dataList, computeBatchSize),
                        computeBatchSize -> updateBatchCommitWithSize(dataList, computeBatchSize)
                ), batchSize);
    }

    /**
     * 根据唯一索引更新
     *
     * @param entity 实体对象，使用唯一索引来作为更新条件
     */
    @ModelFill
    @UpdateProvider(type = PamirsMapperProvider.class)
    int updateByUniqueKey(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 根据 主键 修改
     *
     * @param entity 实体对象，必须包含主键，支持复合主键
     */
    @ModelFill
    @UpdateProvider(type = PamirsMapperProvider.class)
    int updateByPk(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 根据 主键列表 修改
     *
     * @param entity 实体对象
     * @param pks    主键列表(不能为 null 以及 empty)，支持复合主键
     */
    @ModelFill
    @UpdateProvider(type = PamirsMapperProvider.class)
    int updateByPks(@NotNull @Param(Constants.ENTITY) T entity,
                    @NotNull @Param(StatementConstants.CONDITION_COLLECTION) List<T> pks);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    @ModelFill
    @UpdateProvider(type = PamirsMapperProvider.class)
    int update(@NotNull @Param(Constants.ENTITY) T entity,
               @NotNull @Param(Constants.WRAPPER) IWrapper<T> updateWrapper);

    /**
     * 根据 主键 删除
     *
     * @param pk 主键，支持复合主键
     */
    @ModelFill
    @DeleteProvider(type = PamirsMapperProvider.class)
    int deleteByPk(@NotNull @Param(Constants.ENTITY) T pk);

    /**
     * 删除（根据主键 批量删除）
     *
     * @param pks 主键列表(不能为 null 以及 empty)，支持复合主键
     */
    @ModelFill
    @DeleteProvider(type = PamirsMapperProvider.class)
    int deleteByPks(@NotNull @Param(StatementConstants.CONDITION_COLLECTION) List<T> pks);

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param entity 表字段 map 对象
     */
    @ModelFill
    @DeleteProvider(type = PamirsMapperProvider.class)
    int deleteByEntity(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param wrapper 实体对象封装操作类（可以为 null）
     */
    @ModelFill
    @DeleteProvider(type = PamirsMapperProvider.class)
    int delete(@NotNull @Param(Constants.WRAPPER) IWrapper<T> wrapper);

    /**
     * 根据 唯一索引 条件，删除记录
     *
     * @param entity 表字段 map 对象
     */
    @ModelFill
    @DeleteProvider(type = PamirsMapperProvider.class)
    int deleteByUniqueKey(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 删除（根据唯一索引 批量删除）
     *
     * @param entityList 主键列表(不能为 null 以及 empty)，支持复合主键
     */
    @ModelFill
    @DeleteProvider(type = PamirsMapperProvider.class)
    int deleteByUniqueKeys(@NotNull @Param(StatementConstants.CONDITION_COLLECTION) List<T> entityList);

    /**
     * 根据 主键 查询
     *
     * @param pk 主键map，支持复合主键
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    T selectByPk(@NotNull @Param(Constants.ENTITY) T pk);

    /**
     * 根据 wrapper 条件，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    T selectOne(@NotNull @Param(Constants.WRAPPER) IWrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询一条记录
     *
     * @param entity 实体对象
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    T selectOneByEntity(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 根据唯一索引查询单条记录
     *
     * @param entity 带唯一索引实体
     * @return 记录
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    T selectOneByUniqueKey(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 查询（根据主键 批量查询）
     *
     * @param pks 主键map列表(不能为 null 以及 empty)，支持复合主键
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    List<T> selectListByPks(@NotNull @Param(StatementConstants.CONDITION_COLLECTION) List<T> pks);

    /**
     * 查询（根据唯一索引 批量查询）
     *
     * @param entityList 唯一索引map列表(不能为 null 以及 empty)，支持复合索引
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    List<T> selectListByUniqueKey(@NotNull @Param(StatementConstants.CONDITION_COLLECTION) List<T> entityList);

    /**
     * 查询（根据 entity 条件）
     *
     * @param entity 表字段 map 对象
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    List<T> selectListByEntity(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    List<T> selectList(@NotNull @Param(Constants.WRAPPER) IWrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询总记录数
     *
     * @param entity 实体对象
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    @ResultType(Long.class)
    Long selectCountByEntity(@NotNull @Param(Constants.ENTITY) T entity);

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    @ResultType(Long.class)
    Long selectCount(@NotNull @Param(Constants.WRAPPER) IWrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     */
    @ModelFill
    @SelectProvider(type = PamirsMapperProvider.class)
    <E extends Pagination<T>> List<T> selectListByPage(@NotNull @Param(VariableNameConstants.page) E page,
                                                       @NotNull @Param(Constants.WRAPPER) IWrapper<T> queryWrapper);

    @ModelFill
    default <E extends Pagination<T>> E selectPage(@NotNull @Param(VariableNameConstants.page) E page,
                                                   @NotNull @Param(Constants.WRAPPER) IWrapper<T> queryWrapper) {
        page.setContent(selectListByPage(page, queryWrapper));
        return page;
    }

}
