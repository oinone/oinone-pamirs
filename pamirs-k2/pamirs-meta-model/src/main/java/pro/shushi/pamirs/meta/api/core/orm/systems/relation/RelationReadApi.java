package pro.shushi.pamirs.meta.api.core.orm.systems.relation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 关联关系读数据管理
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
public interface RelationReadApi extends CommonApi {

    /**
     * 是否满足前端子查询要求
     *
     * @param modelFieldConfig 字段配置
     * @param fieldValue       字段值
     * @return 是否满足
     */
    boolean isNeedQueryRelation(ModelFieldConfig modelFieldConfig, Object fieldValue);

    /**
     * 获取关联关系字段数据
     *
     * @param modelFieldConfig 模型字段配置
     * @param data             模型数据
     * @return 关联关系字段数据
     */
    <T> Object queryFieldByRelation(ModelFieldConfig modelFieldConfig, T data);

    /**
     * 获取列表的关联关系字段数据
     *
     * @param modelFieldConfig 模型字段配置
     * @param dataList         模型数据列表
     * @return 关联关系字段数据
     */
    <T> List<T> listFieldQueryByRelation(ModelFieldConfig modelFieldConfig, List<T> dataList);

    /**
     * 获取关系数据列表的关联关系字段数据
     *
     * @param keys        关系数据键列表
     * @param keyContexts 上下文
     * @return 关联关系字段数据
     */
    List<Object> listFieldQueryByRelationKey(List<String> keys, Map<Object, Object> keyContexts);

    /**
     * 获取关系数据列表的关联关系字段数据，支持结果器处理
     *
     * @param keyList       关系数据键列表
     * @param keyContexts   上下文
     * @param resultHandler 结果处理器
     * @return 关联关系字段数据
     */
    List<Object> listFieldQueryByRelationKey(List<String> keyList, Map<Object, Object> keyContexts,
                                             BiFunction<ModelFieldConfig, Object, Object> resultHandler);

    /**
     * 查询一对多关联模型或多对多中间模型数据
     *
     * @param modelFieldConfig 模型字段配置
     * @param data             模型数据
     * @return 中间模型数据
     */
    <T, R> List<R> queryOneToManyByRelation(ModelFieldConfig modelFieldConfig, T data);

    /**
     * 生成关系查询条件
     * <p>
     * 多对多生成中间模型的查询条件
     *
     * @param modelFieldConfig 模型字段配置
     * @param data             模型数据
     * @param <T>              模型类型
     * @return 查询条件
     */
    <T, R> IWrapper<R> generateRelationQuery(ModelFieldConfig modelFieldConfig, T data);

}
