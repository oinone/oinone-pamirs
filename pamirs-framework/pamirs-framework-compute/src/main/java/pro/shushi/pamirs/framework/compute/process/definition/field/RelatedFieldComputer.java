package pro.shushi.pamirs.framework.compute.process.definition.field;

import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_RELATED_FIELD_CONFIG_IS_NOT_EXISTS_ERROR;

/**
 * 引用字段计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class RelatedFieldComputer implements FieldComputer<Meta, ModelDefinition> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, ModelDefinition data) {
        Result<Void> result = new Result<>();
        TypeProcessor typeProcessor = CommonApiFactory.getTypeProcessor();
        // 计算引用字段
        fetchRelatedAttribute(new HashMap<>(), typeProcessor, meta, field.getModel(), field, field);
        return result;
    }

    private String fetchRelatedAttribute(Map<String, String> completedRelated,
                                         TypeProcessor typeProcessor, Meta meta, String model, ModelField relatedField, ModelField currentField) {
        String references = model;// related到关系字段时设置
        if (TtypeEnum.RELATED.value().equals(currentField.getTtype().value())) {
            /*
             * case:
             * 1.related 到基本字段
             * 2.related 到关系字段
             * 3.related 到related字段 递归获取
             */
            List<String> relatedFields = currentField.getRelated();
            ModelField relatedModelField;
            TtypeEnum ttype;
            Boolean multi = null;
            boolean hasSerialize = null != relatedField.getStoreSerialize();
            String serialize;
            DateFormatEnum format;
            String dictionary;
            List<DataDictionaryItem> options;
            for (String related : relatedFields) {
                relatedModelField = meta.getModelField(references, related);
                if (null == relatedModelField) {
                    throw PamirsException.construct(BASE_RELATED_FIELD_CONFIG_IS_NOT_EXISTS_ERROR)
                            .appendMsg("[" + model + "] 引用 [" + currentField.getField() + "] 属性, 在 [" + references + "] 中不存在 [" + related + "] 属性").errThrow();
                }
                ttype = relatedModelField.getTtype();
                serialize = relatedModelField.getStoreSerialize();
                format = relatedModelField.getFormat();
                dictionary = relatedModelField.getDictionary();
                options = relatedModelField.getOptions();
                if (typeProcessor.isRelationField(ttype.value())) {
                    references = relatedModelField.getReferences();
                    multi = relatedModelField.getMulti();
                    relatedField.setReferences(references);
                    relatedField.setMulti(multi);
                    configSerialize(relatedField, serialize, !hasSerialize);
                    relatedField.setRelatedTtype(ttype);
                    relatedField.setFormat(format);
                    relatedField.setDictionary(dictionary);
                    relatedField.setOptions(options);
                } else if (TtypeEnum.RELATED.value().equals(ttype.value())) {
                    String completed = references + CharacterConstants.SEPARATOR_AT + related;
                    if (completedRelated.containsKey(completed)) {
                        references = completedRelated.get(completed);
                        relatedField.setReferences(references);
                        relatedField.setMulti(relatedModelField.getMulti());
                        configSerialize(relatedField, relatedModelField.getStoreSerialize(), !hasSerialize);
                        relatedField.setFormat(relatedModelField.getFormat());
                        break;
                    } else {
                        completedRelated.put(completed, references);
                        currentField = meta.getModelField(references, related);
                        references = fetchRelatedAttribute(completedRelated, typeProcessor, meta, references, relatedField, currentField);
                    }
                } else {
                    multi = Boolean.TRUE.equals(multi) ? multi : relatedModelField.getMulti();
                    relatedField.setReferences(references);
                    relatedField.setMulti(multi);
                    configSerialize(relatedField, serialize, !hasSerialize);
                    relatedField.setRelatedTtype(ttype);
                    relatedField.setFormat(format);
                    relatedField.setDictionary(dictionary);
                    relatedField.setOptions(options);
                }
            }
        }
        return references;
    }

    private void configSerialize(ModelField relatedField, String serialize, boolean absent) {
        if (absent) {
            relatedField.setStoreSerialize(serialize);
        }
    }

}
