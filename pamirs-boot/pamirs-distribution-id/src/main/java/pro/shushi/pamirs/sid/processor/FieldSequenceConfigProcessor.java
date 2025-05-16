package pro.shushi.pamirs.sid.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldExtendComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.sid.enmu.SidExpEnumerate;

/**
 * 字段序列配置转换逻辑扩展
 * <p>
 * 2020/4/26 11:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order(2)
@Component
public class FieldSequenceConfigProcessor implements FieldExtendComputer<Meta, ModelDefinition> {

    @Override
    public Result<Void> compute(Meta meta, String model, String field, ModelDefinition data) {
        if (data.isMetaCompleted()) {
            return new Result<>();
        }
        ModelField modelField = meta.getModelField(model, field);
        if (modelField.isMetaCompleted()) {
            return new Result<>();
        }
        SequenceConfig sequenceConfig = modelField.getSequenceConfig();
        if (null != sequenceConfig && Boolean.TRUE.equals(modelField.getSys()) && !SystemSourceEnum.isInherited(sequenceConfig.getSystemSource())) {
            sequenceConfig.setSign(sequenceConfig.getCode());
            SequenceConfig existConfig = meta.getDataItem(SequenceConfig.MODEL_MODEL, sequenceConfig.getCode());
            if(null != existConfig){
                sequenceConfig.setId(existConfig.getId());
                sequenceConfig.setHash(existConfig.getHash());
                sequenceConfig.setStringify(existConfig.getStringify());
            }
            meta.getCurrentModuleData().addData(sequenceConfig);
        }
        if (null == sequenceConfig && StringUtils.isNotBlank(modelField.getSequenceCode())) {
            SequenceConfig existConfig = meta.getDataItem(SequenceConfig.MODEL_MODEL, modelField.getSequenceCode());
            if (null == existConfig) {
                throw PamirsException.construct(SidExpEnumerate.BASE_GENERATE_FIELD_SEQUENCE_CONFIG_NOT_EXIST_ERROR)
                        .appendMsg("model:" + model + ",field:" + field + ",code:" + modelField.getSequenceCode())
                        .errThrow();
            } else {
                modelField.setSequenceConfig(existConfig);
            }
        }
        return new Result<>();
    }
}
