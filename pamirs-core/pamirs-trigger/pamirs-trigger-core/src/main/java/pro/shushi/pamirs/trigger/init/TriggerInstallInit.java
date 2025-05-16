package pro.shushi.pamirs.trigger.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.trigger.TriggerModule;

import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-11-10 11:09
 */
@Component
public class TriggerInstallInit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, TriggerModule.MODULE_MODULE, TriggerModule.MODULE_NAME);
        if (util == null) {
            return;
        }
    }
}
