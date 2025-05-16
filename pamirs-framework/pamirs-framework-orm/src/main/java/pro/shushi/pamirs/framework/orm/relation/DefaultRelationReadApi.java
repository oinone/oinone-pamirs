package pro.shushi.pamirs.framework.orm.relation;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.service.DataSourceRouteService;
import pro.shushi.pamirs.framework.connectors.data.dialect.RelationFieldQueryDialectService;
import pro.shushi.pamirs.framework.connectors.data.holder.RelationFieldQueryDialectServiceHolder;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.relation.RelationKey;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 关联关系读管理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@Component
public class DefaultRelationReadApi implements RelationReadApi {

    private static final HoldKeeper<DataSourceRouteService> holder = new HoldKeeper<>();

    private static DataSourceRouteService getRouteService() {
        return holder.supply(() -> Spider.getDefaultExtension(DataSourceRouteService.class));
    }

    private RelationFieldQueryDialectService getApi(ModelFieldConfig modelField) {
        Object dsKey = PamirsSession.getDsKey();
        if (dsKey == null) {
            dsKey = getRouteService().route(modelField.getModel());
        }
        if (dsKey == null) {
            return RelationFieldQueryDialectServiceHolder.getDefaultService();
        }
        return RelationFieldQueryDialectServiceHolder.get(String.valueOf(dsKey));
    }

    @Override
    public boolean isNeedQueryRelation(ModelFieldConfig modelFieldConfig, Object fieldValue) {
        String references = modelFieldConfig.getReferences();
        List<String> relationFields = modelFieldConfig.getRelationFields();
        //检查关联关系
        if (checkRelation(modelFieldConfig, references, relationFields)) {
            return false;
        }
        if (null == fieldValue) {
            return true;
        } else {
            if (TtypeEnum.isRelationOne(modelFieldConfig.getTtype())) {
                List<String> referenceFields = modelFieldConfig.getReferenceFields();
                if (CollectionUtils.isEmpty(referenceFields)) {
                    return false;
                }
                List<String> ignoreFields = new ArrayList<>();
                int i = 0;
                for (String referenceField : referenceFields) {
                    String relationField = relationFields.get(i);
                    i++;
                    if (FieldUtils.isConstantRelationFieldValue(relationField)) {
                        ignoreFields.add(referenceField);
                        continue;
                    }
                    Object referenceFieldValue = FieldUtils.getReferenceFieldValue(fieldValue, references, referenceField);
                    if (null == referenceFieldValue) {
                        return false;
                    }
                }
                return Models.compute().isOnlyNonEmptyFields(modelFieldConfig.getReferences(), referenceFields, ignoreFields, fieldValue);
            } else {
                return false;
            }
        }
    }

    private static boolean checkRelation(ModelFieldConfig modelFieldConfig, String references, List<String> relationFields) {
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(references);
        ModelTypeEnum type = modelConfig.getType();
        if ((!ModelTypeEnum.STORE.equals(type) && !ModelTypeEnum.PROXY.equals(type))) {
            return true;
        }

        String ttype = modelFieldConfig.getTtype();
        List<String> queryFields;
        String queryModel;
        if (TtypeEnum.M2O.value().equals(ttype) || TtypeEnum.O2O.value().equals(ttype) || TtypeEnum.O2M.value().equals(ttype)) {
            queryFields = modelFieldConfig.getReferenceFields();
            queryModel = modelFieldConfig.getReferences();
        } else if (TtypeEnum.M2M.value().equals(ttype)) {
            queryFields = modelFieldConfig.getThroughRelationFields();
            queryModel = modelFieldConfig.getThrough();
        } else {
            throw PamirsException.construct(OrmExpEnumerate.BASE_TTYPE_IS_NOT_SUPPORT_ERROR)
                    .appendMsg("model:" + modelFieldConfig.getModel() + ",field:" + modelFieldConfig.getField() + ",ttype:" + ttype).errThrow();
        }

        return CollectionUtils.isEmpty(relationFields)
                || CollectionUtils.isEmpty(queryFields)
                || relationFields.size() != queryFields.size()
                || null == queryModel;
    }

    @Override
    public <T> Object queryFieldByRelation(ModelFieldConfig modelFieldConfig, T data) {
        return getApi(modelFieldConfig).queryFieldByRelation(modelFieldConfig, data);
    }

    @Override
    public <T> List<T> listFieldQueryByRelation(ModelFieldConfig modelFieldConfig, List<T> dataList) {
        return getApi(modelFieldConfig).listFieldQueryByRelation(modelFieldConfig, dataList);
    }

    @Override
    public List<Object> listFieldQueryByRelationKey(List<String> keyList, Map<Object, Object> keyContexts) {
        ModelFieldConfig modelFieldConfig = ((RelationKey) keyContexts.get(keyList.get(0))).getModelFieldConfig();
        return getApi(modelFieldConfig).listFieldQueryByRelationKey(keyList, keyContexts);
    }

    @Override
    public List<Object> listFieldQueryByRelationKey(List<String> keyList, Map<Object, Object> keyContexts,
                                                    BiFunction<ModelFieldConfig, Object, Object> resultHandler) {
        ModelFieldConfig modelFieldConfig = ((RelationKey) keyContexts.get(keyList.get(0))).getModelFieldConfig();
        return getApi(modelFieldConfig).listFieldQueryByRelationKey(keyList, keyContexts, resultHandler);
    }

    @Override
    public <T, R> List<R> queryOneToManyByRelation(ModelFieldConfig modelFieldConfig, T data) {
        return getApi(modelFieldConfig).queryOneToManyByRelation(modelFieldConfig, data);
    }

    @Override
    public <T, R> QueryWrapper<R> generateRelationQuery(ModelFieldConfig modelFieldConfig, T data) {
        return getApi(modelFieldConfig).generateRelationQuery(modelFieldConfig, data);
    }
}
