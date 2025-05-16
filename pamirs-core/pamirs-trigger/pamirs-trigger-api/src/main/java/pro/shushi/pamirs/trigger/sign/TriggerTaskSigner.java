package pro.shushi.pamirs.trigger.sign;

import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 13:10
 */
@SPI.Service(TriggerTaskAction.MODEL_MODEL)
public class TriggerTaskSigner implements ModelSigner<TriggerTaskAction> {

    @Override
    public String sign(TriggerTaskAction metaModelObject) {
        return metaModelObject.getTenant() + CharacterConstants.SEPARATOR_COLON + metaModelObject.getEnv() + CharacterConstants.SEPARATOR_COLON + metaModelObject.getTechnicalName();
    }
}
