package pro.shushi.pamirs.meta.api.core.orm.batch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.core.orm.BatchApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.api.enmu.BatchOpTypeEnum;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;

/**
 * 批量操作接口实现
 * 2020/12/16 10:09 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service
public class DefaultBatchApi implements BatchApi {

    @Resource
    private PamirsMapperConfigurationProxy pamirsMapperConfigurationProxy;

    @Override
    public BatchCommitTypeEnum fix(BatchOpTypeEnum opType, String model, BatchCommitTypeEnum operationEnum) {
        if (null == operationEnum) {
            operationEnum = pamirsMapperConfigurationProxy.batch();
        }
        if (BatchOpTypeEnum.insert.equals(opType)) {
            if (PamirsTableInfo.isAutoIncrementModel(model)) {
                return BatchCommitTypeEnum.collectionCommit;
            }
            return operationEnum;
        }
        boolean supportOptimisticLocker = PamirsSession.directive().isOptimisticLocker();
        if (supportOptimisticLocker) {
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
            boolean optimisticLocker = !StringUtils.isBlank(modelConfig.getOptimisticLockerField());
            if (optimisticLocker && (BatchCommitTypeEnum.batchCommit.equals(operationEnum)
                    || BatchCommitTypeEnum.collectionCommit.equals(operationEnum))) {
                operationEnum = BatchCommitTypeEnum.useAndJudgeAffectRows;
            }
        }
        return operationEnum;
    }

}
