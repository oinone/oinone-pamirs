package pro.shushi.pamirs.framework.connectors.data.dialect;

import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.holder.RelationFieldQueryDialectServiceHolder;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 关联字段查询方言服务
 *
 * @author Adamancy Zhang at 18:31 on 2024-10-18
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RelationFieldQueryDialectService {

    <T> Object queryFieldByRelation(ModelFieldConfig modelFieldConfig, T data);

    <T> List<T> listFieldQueryByRelation(ModelFieldConfig modelFieldConfig, List<T> dataList);

    List<Object> listFieldQueryByRelationKey(List<String> keys, Map<Object, Object> keyContexts);

    List<Object> listFieldQueryByRelationKey(List<String> keyList, Map<Object, Object> keyContexts,
                                             BiFunction<ModelFieldConfig, Object, Object> resultHandler);

    <T, R> List<R> queryOneToManyByRelation(ModelFieldConfig modelFieldConfig, T data);

    default <T, R> QueryWrapper<R> generateRelationQuery(ModelFieldConfig modelFieldConfig, T data) {
        return RelationFieldQueryDialectServiceHolder.getDefaultService().generateRelationQuery(modelFieldConfig, data);
    }
}
