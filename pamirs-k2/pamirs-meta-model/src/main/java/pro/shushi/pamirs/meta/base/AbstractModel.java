package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.Date;
import java.util.List;

/**
 * 超级抽象基类
 * <p>
 * 可以设置batchSize来分批次查询或更新，如果设置为-1，则不分批次查询或更新；
 * 如果batchSize设置为0（默认为0），则读取下一级batchSize配置；
 * 读batchSize设置级别：queryWrapper或方法入参 > 启动配置文件（按模型） > 默认每批次查询500条；
 * 写batchSize设置级别：方法入参 > 启动配置文件（按模型） > 默认每批次更新5000条；
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@SuppressWarnings("unchecked")
@MetaSimulator(onlyBasicTypeField = false)
@Base
@Model.model(NamespaceConstants.pamirs)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, name = NamespaceConstants.pamirs, priority = 33)
@Model(displayName = "抽象模型基类", summary = "道生一")
public abstract class AbstractModel extends K2 {

    private static final long serialVersionUID = -7614939125716806495L;

    @Base
    @Field.Advanced(columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP", insertStrategy = FieldStrategyEnum.NEVER, updateStrategy = FieldStrategyEnum.NEVER, batchStrategy = FieldStrategyEnum.NEVER)
    @Field(displayName = "创建时间", priority = 200)
    private Date createDate;

    @Base
    @Field.Advanced(columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", batchStrategy = FieldStrategyEnum.NEVER)
    @Field(displayName = "更新时间", priority = 210)
    private Date writeDate;

    @Base
    @Field(displayName = "创建人ID", priority = 220, invisible = true)
    private Long createUid;

    @Base
    @Field(displayName = "更新人ID", priority = 230, invisible = true)
    private Long writeUid;

    /*
     * 数据系统
     */

    /**
     * 数据管理器
     */
    private OriginDataManager origin() {
        return Models.origin();
    }

    /**
     * 创建数据
     *
     * @param <T> 模型类型
     * @return 模型数据
     */
    public <T extends AbstractModel> T create() {
        return origin().createOne((T) this);
    }

    /**
     * 批量创建数据
     *
     * @param dataList 待新增的数据
     * @param <T>      模型类型
     * @return 模型数据
     */
    public <T extends AbstractModel> List<T> createBatch(List<T> dataList) {
        return origin().createBatch(dataList);
    }

    /**
     * 创建或更新数据
     * <br/>
     * 若无主键且无唯一索引字段或主键、唯一索引字段数据为空，则新增数据
     *
     * @return 影响行数
     */
    public Integer createOrUpdate() {
        return origin().createOrUpdate(this);
    }

    /**
     * 创建或更新数据
     * <br/>
     * 若无主键且无唯一索引字段或主键、唯一索引字段数据为空，则新增数据
     *
     * @return UpdatResult
     */
    public <T extends AbstractModel> Result<T> createOrUpdateWithResult() {
        return origin().createOrUpdateWithResult((T) this);
    }

    /**
     * 批量创建或更新数据
     * <br/>
     * 若无主键且无唯一索引字段或主键、唯一索引字段数据为空，则新增数据
     *
     * @param dataList 待新增的数据
     * @param <T>      模型类型
     * @return 影响行数
     */
    public <T extends AbstractModel> Integer createOrUpdateBatch(List<T> dataList) {
        return origin().createOrUpdateBatch(dataList);
    }

    /**
     * 批量创建或更新数据
     * <br/>
     * 若无主键且无唯一索引字段或主键、唯一索引字段数据为空，则新增数据
     *
     * @param dataList 待新增的数据
     * @param <T>      模型类型
     * @return UpdatResult<List < T>>
     */
    public <T extends AbstractModel> Result<List<T>> createOrUpdateBatchWithResult(List<T> dataList) {
        return origin().createOrUpdateBatchWithResult(dataList);
    }

    /**
     * 批量更新数据
     * <br/>
     * 模型数据中必须包含主键或至少一个唯一索引
     *
     * @param dataList 待新增的数据
     * @param <T>      模型类型
     * @return 影响行数
     */
    public <T extends AbstractModel> Integer updateBatch(List<T> dataList) {
        return origin().updateBatch(dataList);
    }

    /**
     * 根据主键更新数据
     * <br/>
     * 模型数据中必须包含主键
     *
     * @return 影响行数
     */
    public Integer updateByPk() {
        return origin().updateByPk(this);
    }

    /**
     * 根据主键与唯一索引更新数据
     * <br/>
     * 模型数据中必须包含主键或至少一个唯一索引
     *
     * @return 影响行数
     */
    public Integer updateByUnique() {
        return origin().updateByUniqueField(this);
    }

    /**
     * 条件更新
     *
     * @param entity 更新实体
     * @param query  更新条件
     * @param <T>    模型类型
     * @return 影响行数
     */
    public <T extends AbstractModel> Integer updateByEntity(T entity, T query) {
        return origin().updateByEntity(entity, query);
    }

    /**
     * 条件更新
     *
     * @param entity        更新实体
     * @param updateWrapper 更新条件
     * @param <T>           模型类型
     * @return 影响行数
     */
    public <T extends AbstractModel> Integer updateByWrapper(T entity, IWrapper<T> updateWrapper) {
        if (updateWrapper.getModel() == null) {
            updateWrapper.setModel(Models.api().getDataModel(this));
        }
        return origin().updateByWrapper(entity, updateWrapper);
    }

    /**
     * 根据主键删除单条记录
     * <br/>
     * 模型数据中必须包含主键
     *
     * @return 删除结果
     */
    public Boolean deleteByPk() {
        return origin().deleteByPk(this);
    }

    /**
     * 根据主键批量删除记录
     * <br/>
     * 模型数据中必须包含主键
     *
     * @return 删除结果
     */
    public <T extends AbstractModel> Boolean deleteByPks(List<T> dataList) {
        return origin().deleteByPks(dataList);
    }

    /**
     * 根据主键或唯一索引删除单条记录
     * <br/>
     * 模型数据中必须包含主键或者至少一个唯一索引
     *
     * @return 删除结果
     */
    public Boolean deleteByUnique() {
        return origin().deleteByUniqueField(this);
    }

    /**
     * 根据主键或唯一索引批量删除记录
     * <br/>
     * 模型数据中必须包含主键或者至少一个唯一索引
     *
     * @return 删除结果
     */
    public <T extends AbstractModel> Boolean deleteByUniques(List<T> dataList) {
        return origin().deleteByUniques(dataList);
    }

    /**
     * 根据实体删除记录
     *
     * @return 删除结果
     */
    public Integer deleteByEntity() {
        return origin().deleteByEntity(this);
    }

    /**
     * 条件删除
     *
     * @param queryWrapper 删除条件
     * @param <T>          模型类型
     * @return 删除函数
     */
    public <T extends AbstractModel> Integer deleteByWrapper(IWrapper<T> queryWrapper) {
        if (queryWrapper.getModel() == null) {
            queryWrapper.setModel(Models.api().getDataModel(this));
        }
        return origin().deleteByWrapper(queryWrapper);
    }

    /**
     * 根据主键查询单条记录
     * <br/>
     * 模型数据中必须包含主键
     *
     * @param <T> 模型类型
     * @return 模型数据
     */
    public <T extends AbstractModel> T queryByPk() {
        return origin().queryByPk((T) this);
    }

    /**
     * 根据主键或唯一索引查询单条记录
     * <br/>
     * 模型数据中必须包含主键或唯一索引，查出多条记录会抛异常
     *
     * @param <T> 模型类型
     * @return 模型数据
     */
    public <T extends AbstractModel> T queryOne() {
        return origin().queryOne((T) this);
    }

    /**
     * 根据条件查询单条记录
     * <br/>
     * 查出多条记录会抛异常
     *
     * @param <T> 模型类型
     * @return 模型数据
     */
    public <T extends AbstractModel> T queryOneByWrapper(IWrapper<T> queryWrapper) {
        if (queryWrapper.getModel() == null) {
            queryWrapper.setModel(Models.api().getDataModel(this));
        }
        return origin().queryOneByWrapper(queryWrapper);
    }

    /**
     * 按实体条件查询数据列表
     *
     * @param <T> 模型类型
     * @return 满足条件的数据记录列表
     */
    public <T extends AbstractModel> List<T> queryList() {
        return origin().queryListByEntity((T) this);
    }

    /**
     * 按实体条件查询数据列表，可以设置batchSize
     *
     * @param <T> 模型类型
     * @return 满足条件的数据记录列表
     */
    public <T extends AbstractModel> List<T> queryList(int batchSize) {
        return origin().queryListByEntityWithBatchSize((T) this, batchSize);
    }

    /**
     * 按条件查询数据列表
     *
     * @param queryWrapper 查询条件
     * @param <T>          模型类型
     * @return 满足条件的数据记录列表
     */
    public <T extends AbstractModel> List<T> queryList(IWrapper<T> queryWrapper) {
        if (queryWrapper.getModel() == null) {
            queryWrapper.setModel(Models.api().getDataModel(this));
        }
        return origin().queryListByWrapper(queryWrapper);
    }

    /**
     * 按分页和实体条件查询数据列表
     *
     * @param page  分页
     * @param query 查询实体
     * @param <T>   模型类型
     * @return 查询分页结果
     */
    public <T extends AbstractModel> List<T> queryList(Pagination<T> page, T query) {
        return origin().queryListByEntity(page, query);
    }

    /**
     * 按分页条件查询数据列表
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @param <T>          模型类型
     * @return 查询分页结果
     */
    public <T extends AbstractModel> List<T> queryListByWrapper(Pagination<T> page, IWrapper<T> queryWrapper) {
        if (queryWrapper.getModel() == null) {
            queryWrapper.setModel(Models.api().getDataModel(this));
        }
        return origin().queryListByWrapper(page, queryWrapper);
    }

    /**
     * 按条件查询数据列表
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @param <T>          模型类型
     * @return 查询分页结果
     */
    public <T extends AbstractModel> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        if (queryWrapper.getModel() == null) {
            queryWrapper.setModel(Models.api().getDataModel(this));
        }
        return origin().queryPage(page, queryWrapper);
    }

    /**
     * 按实体条件查询数据数量
     *
     * @return 满足查询条件的记录数量
     */
    public Long count() {
        return origin().count(this);
    }

    /**
     * 按条件查询数据数量
     *
     * @param queryWrapper 查询条件
     * @param <T>          模型类型
     * @return 满足查询条件的记录数量
     */
    public <T extends AbstractModel> Long count(IWrapper<T> queryWrapper) {
        if (queryWrapper.getModel() == null) {
            queryWrapper.setModel(Models.api().getDataModel(this));
        }
        return origin().count(queryWrapper);
    }

    /*属性查询*/

    /**
     * 关联关系字段查询
     *
     * @param getter 关联关关系字段getter方法，例如Model::getField
     * @param <T>    模型类型
     * @return 返回包含查询字段值得模型数据
     */
    public <T extends AbstractModel> T fieldQuery(Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return fieldQuery(fieldName);
    }

    /**
     * 关联关系字段查询
     *
     * @param fieldName java字段名称
     * @param <T>       模型类型
     * @return 返回包含查询字段值得模型数据
     */
    public <T extends AbstractModel> T fieldQuery(String fieldName) {
        return (T) origin().fieldQuery(this, fieldName);
    }

    /**
     * 新增或更新关联关系字段（增量）
     * <br/>
     * 根据指令系统的数据提交策略新增或更新关联关系字段，如果未设置指令，则使用默认提交策略<br/>
     * 默认提交策略：<br/>
     * 全量更新会完全按照当前关联关系字段值处理关系<br/>
     * 当前关联关系字段值中未包含的已存在数据库中的关联关系将被删除<br/>
     * 当前关联关系字段值中包含的已存数据库中在的关联关系的关联记录将被更新<br/>
     * 当前关联关系字段值中包含的不存在数据库中的关联关系和关联记录将被创建<br/>
     *
     * @param getter 关联关关系字段getter方法，例如Model::getField
     * @param <T>    模型类型
     * @return 返回包含更新字段值得模型数据
     */
    public <T extends AbstractModel> T fieldSave(Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return fieldSave(fieldName);
    }

    /**
     * 新增或更新关联关系字段（增量）
     *
     * <p>当前模型与字段值都为新创建，如果字段在关系中是one，执行该方法后再保存当前模型实体.
     * 其他场景先保存模型实体后，再调用此方法
     *
     * @param fieldName java字段名称
     * @param <T>       模型类型
     * @return 返回包含更新字段值得模型数据
     */
    public <T extends AbstractModel> T fieldSave(String fieldName) {
        return (T) origin().fieldSave(this, fieldName);
    }

    /**
     * 批量新增或更新关联关系字段（全量）
     * <p>
     * 并按照字段级联策略处理旧记录的关系数据（如：删除、SET_NULL），通过 Getter 方法指定关联关系字段。返回包含更新字段值的模型数据列表。
     *
     * @param <T>    模型类型
     * @param getter 关联关关系字段getter方法，例如Model::getField
     * @return 返回包含更新字段值的模型数据
     */
    public <T extends AbstractModel> T fieldSaveOnCascade(Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return fieldSaveOnCascade(fieldName);
    }

    /**
     * 批量新增或更新关联关系字段（全量）
     * <p>
     * 并按照字段级联策略处理旧记录的关系数据（如：删除、SET_NULL），通过 Getter 方法指定关联关系字段。返回包含更新字段值的模型数据列表。
     *
     * @param <T>       模型类型
     * @param fieldName java字段名称
     * @return 返回包含更新字段值的模型数据
     */
    public <T extends AbstractModel> T fieldSaveOnCascade(String fieldName) {
        return (T) origin().fieldSaveOnCascade(this, fieldName);
    }

    /**
     * 删除关联关系（增量）
     *
     * @param getter 关联关关系字段getter方法，例如Model::getField
     * @param <T>    模型类型
     * @return 返回模型数据
     */
    public <T extends AbstractModel> T relationDelete(Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return relationDelete(fieldName);
    }

    /**
     * 删除关联关系（增量）
     *
     * @param fieldName java字段名称
     * @param <T>       模型类型
     * @return 返回模型数据
     */
    public <T extends AbstractModel> T relationDelete(String fieldName) {
        return (T) origin().relationDelete(this, fieldName);
    }

    /**
     * 批量关联关系字段查询
     *
     * @param dataList 当前模型数据列表
     * @param getter   关联关关系字段getter方法，例如Model::getField
     * @param <T>      模型类型
     * @return 返回包含查询字段值得模型数据
     */
    public <T extends AbstractModel> List<T> listFieldQuery(List<T> dataList, Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listFieldQuery(dataList, fieldName);
    }

    /**
     * 批量关联关系字段查询
     *
     * @param dataList  当前模型数据列表
     * @param fieldName java字段名称
     * @param <T>       模型类型
     * @return 返回包含查询字段值得模型数据
     */
    public <T extends AbstractModel> List<T> listFieldQuery(List<T> dataList, String fieldName) {
        return origin().listFieldQuery(dataList, fieldName);
    }

    /**
     * 批量新增或更新关联关系字段记录
     *
     * @param dataList 当前模型数据列表
     * @param getter   关联关关系字段getter方法，例如Model::getField
     * @param <T>      模型类型
     * @return 返回包含更新字段值的模型数据
     */
    @SuppressWarnings({"unused"})
    public <T extends AbstractModel> List<T> listFieldSave(List<T> dataList, Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listFieldSave(dataList, fieldName);
    }

    /**
     * 批量新增或更新关联关系字段记录
     *
     * @param dataList  当前模型数据列表
     * @param fieldName java字段名称
     * @param <T>       模型类型
     * @return 返回包含更新字段值的模型数据
     */
    public <T extends AbstractModel> List<T> listFieldSave(List<T> dataList, String fieldName) {
        return origin().listFieldSave(dataList, fieldName);
    }

    /**
     * 批量新增或更新关联关系字段（全量）
     * <p>
     * 并按照字段级联策略处理旧记录的关系数据（如：删除、SET_NULL），通过 Getter 方法指定关联关系字段。返回包含更新字段值的模型数据列表。
     *
     * @param dataList 当前模型数据列表
     * @param getter   关联关关系字段getter方法，例如Model::getField
     * @param <T>      模型类型
     * @return 返回包含更新字段值的模型数据
     */
    public <T extends AbstractModel> List<T> listFieldSaveOnCascade(List<T> dataList, Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listFieldSaveOnCascade(dataList, fieldName);
    }

    /**
     * 批量新增或更新关联关系字段（全量）
     * <p>
     * 并按照字段级联策略处理旧记录的关系数据（如：删除、SET_NULL），通过 Getter 方法指定关联关系字段。返回包含更新字段值的模型数据列表。
     *
     * @param dataList  当前模型数据列表
     * @param fieldName java字段名称
     * @param <T>       模型类型
     * @return 返回包含更新字段值的模型数据
     */
    public <T extends AbstractModel> List<T> listFieldSaveOnCascade(List<T> dataList, String fieldName) {
        return origin().listFieldSaveOnCascade(dataList, fieldName);
    }

    /**
     * 批量删除关联关系（增量）
     *
     * @param dataList 当前模型数据列表
     * @param getter   关联关关系字段getter方法，例如Model::getField
     * @param <T>      模型类型
     * @return 返回包含更新字段值得模型数据
     */
    @SuppressWarnings({"unused"})
    public <T extends AbstractModel> List<T> listRelationDelete(List<T> dataList, Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listRelationDelete(dataList, fieldName);
    }

    /**
     * 批量删除关联关系（增量）
     *
     * @param dataList  当前模型数据列表
     * @param fieldName java字段名称
     * @param <T>       模型类型
     * @return 返回包含更新字段值得模型数据
     */
    public <T extends AbstractModel> List<T> listRelationDelete(List<T> dataList, String fieldName) {
        return origin().listRelationDelete(dataList, fieldName);
    }

    /**
     * 刷新更新时间
     *
     * @param <T> 模型类型
     * @return 返回当前模型
     */
    public <T extends AbstractModel> T enableRefreshWriteDate() {
        this.get_d().remove(FieldConstants._disableRefreshWriteDate);
        return (T) this;
    }

    /**
     * 刷新更新人
     *
     * @param <T> 模型类型
     * @return 返回当前模型
     */
    public <T extends AbstractModel> T enableRefreshWriteUid() {
        this.get_d().remove(FieldConstants._disableRefreshWriteUid);
        return (T) this;
    }

    /**
     * 不刷新更新时间
     *
     * @param <T> 模型类型
     * @return 返回当前模型
     */
    public <T extends AbstractModel> T disableRefreshWriteDate() {
        this.get_d().put(FieldConstants._disableRefreshWriteDate, true);
        return (T) this;
    }

    /**
     * 不刷新更新人
     *
     * @param <T> 模型类型
     * @return 返回当前模型
     */
    public <T extends AbstractModel> T disableRefreshWriteUid() {
        this.get_d().put(FieldConstants._disableRefreshWriteUid, true);
        return (T) this;
    }

}
