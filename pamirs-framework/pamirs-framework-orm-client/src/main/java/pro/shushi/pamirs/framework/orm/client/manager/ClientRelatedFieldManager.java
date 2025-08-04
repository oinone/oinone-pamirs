package pro.shushi.pamirs.framework.orm.client.manager;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelatedFieldQueryApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.model.RelatedValue;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_REL_FIELD_CONFIG_IS_NOT_EXISTS_ERROR;

/**
 * 前端引用字段转换服务
 *
 * @author d@shushi.pro
 * @author zbh
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientRelatedFieldManager implements RelatedFieldQueryApi {

    @Resource
    private RelationReadApi relationReadApi;

    @SuppressWarnings("rawtypes")
    @Override
    public RelatedValue queryRelated(ModelFieldConfig modelFieldConfig, Object data) {
        RelatedValue relatedValueWrapper = new RelatedValue().setRelatedModel(modelFieldConfig.getModel());
        Object fieldValue = FieldUtils.getFieldValue(data, modelFieldConfig.getLname());
        if (null != fieldValue) {
            return relatedValueWrapper.setRelatedValue(fieldValue);
        }
        List<String> related = modelFieldConfig.getRelated();
        if (!CollectionUtils.isEmpty(related)) {
            String currentRelatedModel = modelFieldConfig.getModel();
            Object relatedValue = data;
            int i = 0;
            for (String relatedField : related) {
                i++;
                ModelFieldConfig relatedFieldConfig = PamirsSession.getContext().getModelField(currentRelatedModel, relatedField);
                if (null == relatedFieldConfig) {
                    throw PamirsException.construct(BASE_REL_FIELD_CONFIG_IS_NOT_EXISTS_ERROR)
                            .appendMsg("field: " + relatedField).errThrow();
                }
                Object oldRelatedValue = relatedValue;
                relatedValue = fetchFieldValue(relatedValue, relatedFieldConfig);
                if (TtypeEnum.isRelationType(relatedFieldConfig.getTtype())) {
                    if (relationReadApi.isNeedQueryRelation(relatedFieldConfig, relatedValue)) {
                        relatedValue = CommonApiFactory.getApi(RelationReadApi.class).queryFieldByRelation(relatedFieldConfig, oldRelatedValue);
                        if (null == relatedValue) {
                            break;
                        }
                        if (relationReadApi.isNeedQueryRelation(relatedFieldConfig, relatedValue)) {
                            FieldUtils.setFieldValue(oldRelatedValue, relatedFieldConfig.getLname(), relatedValue);
                        }
                    } else {
                        if (null == relatedValue) {
                            break;
                        }
                    }
                    currentRelatedModel = relatedFieldConfig.getReferences();
                } else if (TtypeEnum.RELATED.value().equals(relatedFieldConfig.getTtype())) {
                    boolean needRelated = i != related.size() && relationReadApi.isNeedQueryRelation(relatedFieldConfig, relatedValue);
                    if (null == relatedValue) {
                        relatedValue = oldRelatedValue;
                    } else if (!needRelated) {
                        break;
                    }
                    if (null != relatedFieldConfig.getMulti() && relatedFieldConfig.getMulti() && needRelated) {
                        List<Object> relatedValues = new ArrayList<>();
                        if (null == relatedValue) {
                            break;
                        } else {
                            for (Object item : (List) relatedValue) {
                                RelatedValue tempRelated = queryRelated(relatedFieldConfig, item);
                                currentRelatedModel = tempRelated.getRelatedModel();
                                relatedValues.add(tempRelated.getRelatedValue());
                            }
                            relatedValue = relatedValues;
                        }
                    } else {
                        RelatedValue tempRelated = queryRelated(relatedFieldConfig, relatedValue);
                        currentRelatedModel = tempRelated.getRelatedModel();
                        relatedValue = tempRelated.getRelatedValue();
                        if (null == relatedValue) {
                            break;
                        }
                    }
                } else {
                    if (null == relatedValue) {
                        break;
                    }
                }
            }
            return relatedValueWrapper.setRelatedModel(currentRelatedModel).setRelatedValue(relatedValue);
        }
        relatedValueWrapper.setRelatedValue(FieldUtils.getFieldValue(data, modelFieldConfig.getLname()));
        return relatedValueWrapper;
    }

    @SuppressWarnings("rawtypes")
    private Object fetchFieldValue(Object relatedValue, ModelFieldConfig relatedFieldConfig) {
        if (relatedValue instanceof List) {
            List<Object> relatedValues = new ArrayList<>();
            for (Object item : (List) relatedValue) {
                relatedValues.add(FieldUtils.getFieldValue(item, relatedFieldConfig.getLname()));
            }
            return relatedValues;
        } else {
            return FieldUtils.getFieldValue(relatedValue, relatedFieldConfig.getLname());
        }
    }

}
