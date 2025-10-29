package pro.shushi.pamirs.draft.init;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.boot.web.utils.ClientActionUtils;
import pro.shushi.pamirs.boot.web.utils.UiActionUtils;
import pro.shushi.pamirs.draft.DraftModule;
import pro.shushi.pamirs.draft.constant.DraftConstants;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 保存草稿客户端动作初始化
 *
 * @author Adamancy Zhang at 11:57 on 2025-10-22
 */
@Order
@Component
public class DraftClientActionInit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        Set<String> modules = metaMap.values().stream()
                .filter(v -> v.getData().containsKey(DraftModule.MODULE_MODULE))
                .map(Meta::getModule)
                .collect(Collectors.toSet());
        for (String module : modules) {
            UiActionUtils.doSomethingForMenuModel(metaMap, module, this::makeDefaultModelViewAction);
        }
    }

    private void makeDefaultModelViewAction(Meta meta, ModelDefinition data) {
        String module = data.getModule();
        String model = data.getModel();
        String actionName = DraftConstants.SaveDraft.name;
        ClientActionUtils.makeDefaultClientAction(meta, module, model,
                actionName,
                DraftConstants.SaveDraft.displayName,
                DraftConstants.SaveDraft.fun,
                ActionContextTypeEnum.SINGLE,
                DraftConstants.SaveDraft.priority);
        String sign = ClientAction.sign(model, actionName);
        ClientAction action = meta.getData().get(module)
                .getDataItem(ClientAction.MODEL_MODEL, sign);
        action.setBindingType(Lists.newArrayList(ViewTypeEnum.FORM));
    }
}
