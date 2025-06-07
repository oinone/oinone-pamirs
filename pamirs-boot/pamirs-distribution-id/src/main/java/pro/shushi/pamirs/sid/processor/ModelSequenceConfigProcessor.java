package pro.shushi.pamirs.sid.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelExtendComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.sid.enmu.SidExpEnumerate;

/**
 * 模型序列配置转换逻辑扩展
 * <p>
 * 2020/4/26 11:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(2)
@Component
public class ModelSequenceConfigProcessor implements ModelExtendComputer<Meta, ModelDefinition> {

    @Override
    public Result<Void> compute(Meta meta, String model, ModelDefinition data) {
        if (data.isMetaCompleted()) {
            return new Result<>();
        }
        SequenceConfig sequenceConfig = data.getSequenceConfig();
        if (null != sequenceConfig && Boolean.TRUE.equals(data.getSys()) && data.getModel().equals(sequenceConfig.getCode())) {
            sequenceConfig.setSign(sequenceConfig.getCode());
            SequenceConfig existConfig = meta.getDataItem(SequenceConfig.MODEL_MODEL, sequenceConfig.getCode());
            if (null != existConfig) {
                sequenceConfig.setId(existConfig.getId());
                sequenceConfig.setHash(existConfig.getHash());
                sequenceConfig.setStringify(existConfig.getStringify());
            }
            meta.getData().get(data.getModule()).addData(sequenceConfig);
        }
        if (null == sequenceConfig && StringUtils.isNotBlank(data.getSequenceCode())) {
            SequenceConfig existConfig = meta.getDataItem(SequenceConfig.MODEL_MODEL, data.getSequenceCode());
            if (null == existConfig) {
                throw PamirsException.construct(SidExpEnumerate.BASE_GENERATE_MODEL_SEQUENCE_CONFIG_NOT_EXIST_ERROR)
                        .appendMsg("model:" + model + ",code:" + data.getSequenceCode())
                        .errThrow();
            } else {
                data.setSequenceConfig(existConfig);
            }
        }
        return new Result<>();
    }

}
