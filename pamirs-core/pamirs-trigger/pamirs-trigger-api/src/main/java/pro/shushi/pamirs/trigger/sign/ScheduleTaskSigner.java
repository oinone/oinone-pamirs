package pro.shushi.pamirs.trigger.sign;

import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 13:10
 */
@SPI.Service(ScheduleTaskAction.MODEL_MODEL)
public class ScheduleTaskSigner implements ModelSigner<ScheduleTaskAction> {

    @Override
    public String sign(ScheduleTaskAction metaModelObject) {
        return metaModelObject.getTenant() + CharacterConstants.SEPARATOR_COLON + metaModelObject.getEnv() + CharacterConstants.SEPARATOR_COLON + metaModelObject.getTechnicalName();
    }
}
