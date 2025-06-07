package pro.shushi.pamirs.trigger.sign;

import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.trigger.annotation.Trigger;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

import java.lang.reflect.Method;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 13:10
 */
@SPI.Service(TriggerTaskAction.MODEL_MODEL)
public class TriggerTaskReflectSigner implements ModelReflectSigner<TriggerTaskAction, Method> {

    @Override
    public String sign(MetaNames names, Method source) {
        Trigger trigger = source.getAnnotation(Trigger.class);
        if (trigger == null) {
            return null;
        } else {
            return PamirsTenantSession.getTenant() + CharacterConstants.SEPARATOR_COLON + PamirsTenantSession.getEnv() + CharacterConstants.SEPARATOR_COLON + trigger.name();
        }
    }

}
