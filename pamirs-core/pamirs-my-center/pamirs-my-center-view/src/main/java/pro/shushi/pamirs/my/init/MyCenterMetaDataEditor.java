package pro.shushi.pamirs.my.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.my.MyCenterModule;

import java.util.Map;

/**
 * TranslateMetaDataEditor
 *
 * @author yakir on 2020/05/11 12:13.
 */
@Component
public class MyCenterMetaDataEditor implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, MyCenterModule.MODULE_MODULE, MyCenterModule.MODULE_NAME);
        if (util == null) {
            return;
        }

        util.setHomepageByMenu("MyCenterMenus_SettingMenu");
    }

}
