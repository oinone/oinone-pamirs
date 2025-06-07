package pro.shushi.pamirs.business.view.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.loader.view.ViewAutoLoader;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.Map;

/**
 * {@link BusinessModule}元数据编辑
 *
 * @author Adamancy Zhang at 13:44 on 2021-08-31
 */
@Component
public class BusinessMetadataEdit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, BusinessModule.MODULE_MODULE, BusinessModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        ViewAutoLoader.defaultLoad(util);
    }
}
