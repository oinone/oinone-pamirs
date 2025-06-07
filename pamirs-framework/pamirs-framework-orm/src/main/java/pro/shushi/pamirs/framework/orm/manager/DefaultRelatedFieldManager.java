package pro.shushi.pamirs.framework.orm.manager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.serialize.SerializeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelatedFieldManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.io.Serializable;
import java.util.*;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_FIELD_CONFIG_IS_NOT_EXISTS_ERROR;
import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_RELATION_FIELD_CONFLICT_ERROR;

/**
 * 引用字段转换服务
 *
 * @author d@shushi.pro
 * @author zbh
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class DefaultRelatedFieldManager implements RelatedFieldManager {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void fillRelatedFieldValueFromRelation(ModelFieldConfig fieldConfig, Map<String, Object> dMap) {
        Boolean relatedInternalStore = fieldConfig.getModelField().getRelatedInternalStore();
        if (relatedInternalStore == null) {
            relatedInternalStore = Boolean.TRUE;
        }
        if (!relatedInternalStore) {
            return;
        }
        // 引用字段处理
        if (fieldConfig.getTtype().equals(TtypeEnum.RELATED.value()) && !CollectionUtils.isEmpty(fieldConfig.getRelated()) && fieldConfig.getStore()) {
            String currentModel = fieldConfig.getModel();
            Object currentRelated = dMap;
            for (String relatedField : fieldConfig.getRelated()) {
                if (null != currentRelated) {
                    final String finalCurrentModel = currentModel;
                    ModelFieldConfig relationFieldConfig = Optional.ofNullable(PamirsSession.getContext())
                            .map(v -> v.getModelField(finalCurrentModel, relatedField)).orElse(null);
                    if (null == relationFieldConfig) {
                        throw PamirsException.construct(BASE_FIELD_CONFIG_IS_NOT_EXISTS_ERROR)
                                .appendMsg("model:" + finalCurrentModel + ",field:" + relatedField).errThrow();
                    }
                    if (currentRelated instanceof List) {
                        List<Object> currentRelateds = new ArrayList<>();
                        for (Object o : Collections.unmodifiableList((List) currentRelated)) {
                            currentRelateds.add(FieldUtils.getFieldValue(o, relationFieldConfig.getLname()));
                        }
                        currentRelated = currentRelateds;
                    } else {
                        currentRelated = FieldUtils.getFieldValue(currentRelated, relationFieldConfig.getLname());
                    }
                    currentModel = relationFieldConfig.getReferences();
                }
            }
            if (null != currentRelated && null != dMap.get(fieldConfig.getLname())) {
                Object currentRelatedString = null;
                Object existRelatedString = null;
                if (!TypeUtils.isBaseType(fieldConfig.getLtype())) {
                    currentRelatedString = CommonApiFactory.getApi(SerializeProcessor.class)
                            .serialize(fieldConfig.getStoreSerialize(), fieldConfig.getLtype(), currentRelated);
                    existRelatedString = CommonApiFactory.getApi(SerializeProcessor.class)
                            .serialize(fieldConfig.getStoreSerialize(), fieldConfig.getLtype(), dMap.get(fieldConfig.getLname()));
                }
                if (null != currentRelatedString && null != existRelatedString && !existRelatedString.equals(currentRelatedString)) {
                    throw PamirsException.construct(BASE_RELATION_FIELD_CONFLICT_ERROR).appendMsg("field:" + fieldConfig.getLname()).errThrow();
                }
            }
            if (null != dMap && !dMap.containsKey(fieldConfig.getLname()) && null != currentRelated) {
                dMap.put(fieldConfig.getLname(), currentRelated);
            }
        }
    }

    @Override
    public void deSerializeStoreRelatedFieldValue(ModelFieldConfig fieldConfig, Map<String, Object> dMap) {
        // 引用字段处理
        if (fieldConfig.getTtype().equals(TtypeEnum.RELATED.value()) && !CollectionUtils.isEmpty(fieldConfig.getRelated()) && fieldConfig.getStore()) {
            Object relatedValue = FieldUtils.getFieldValue(dMap, fieldConfig.getLname());
            if (null == relatedValue) {
                return;
            }
            if (TypeUtils.isStringType(relatedValue.getClass()) && StringUtils.isBlank((String) relatedValue)) {
                return;
            }
            String ltype = fieldConfig.getLtype();
            if (TypeUtils.isIEnumClass(ltype)) {
                Class<IEnum<?>> ltypeClazz = TypeUtils.getClass(ltype);
                relatedValue = Enums.getEnumByValue(ltypeClazz, (Serializable) relatedValue);
            } else if (relatedValue instanceof String && !TypeUtils.isBaseType(ltype)) {
                relatedValue = Spider.getDefaultExtension(SerializeProcessor.class)
                        .deserialize(fieldConfig.getStoreSerialize(), fieldConfig.getLtype(),
                                fieldConfig.getLtypeT(), fieldConfig.getFormat(), (String) relatedValue);
            }
            if (null != relatedValue) {
                dMap.put(fieldConfig.getLname(), relatedValue);
            }
        }
    }

}
