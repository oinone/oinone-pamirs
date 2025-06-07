package pro.shushi.pamirs.channel.core.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.channel.ChannelModule;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.Map;

@Component
public class ChannelModuleInit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, ChannelModule.MODULE_MODULE, ChannelModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        menuInit(util);
        homepageInit(util);
    }

    private void menuInit(InitializationUtil util) {
        util.createViewActionMenu("增强模型列表", "增强模型列表", 1L, null, ChannelModel.MODEL_MODEL, null, null);
    }

    private void homepageInit(InitializationUtil util) {
        util.setHomepageByMenu("增强模型列表");
    }
}
