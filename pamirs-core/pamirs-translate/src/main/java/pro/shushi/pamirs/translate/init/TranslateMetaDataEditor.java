package pro.shushi.pamirs.translate.init;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.TranslateModule;

import java.util.Map;

/**
 * TranslateMetaDataEditor
 *
 * @author yakir on 2020/05/11 12:13.
 */
@Order
@Component
public class TranslateMetaDataEditor implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, TranslateModule.MODULE_MODULE, TranslateModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        viewActionInit(util);
    }

    private void viewActionInit(InitializationUtil util) {
        util.createViewAction("createResourceTranslationItem",
                "创建",
                ResourceTranslationItem.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE),
                ResourceTranslationItem.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG,
                "translationForm");

    }

}
