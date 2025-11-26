package pro.shushi.pamirs.draft.metadata;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataPreEditor;
import pro.shushi.pamirs.draft.DraftModule;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 草稿模块元数据计算预处理
 * <p>
 * 此元数据预处理用于无需显示依赖即可参与跨模块元数据计算的场景
 *
 * @author Adamancy Zhang at 17:58 on 2025-11-25
 */
@Order(999)
@Component
public class DraftModuleMetaDataPreEditor implements MetaDataPreEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        List<Meta> lazyMetaList = new ArrayList<>();
        Meta draftMeta = null;
        for (Meta meta : metaMap.values()) {
            String module = meta.getModule();
            if (ModuleConstants.MODULE_BASE.equals(module)) {
                continue;
            }
            if (DraftModule.MODULE_MODULE.equals(module)) {
                draftMeta = meta;
                continue;
            }
            if (!meta.getData().containsKey(DraftModule.MODULE_MODULE)) {
                if (draftMeta == null) {
                    lazyMetaList.add(meta);
                } else {
                    meta.getData().put(DraftModule.MODULE_MODULE, draftMeta.getCurrentModuleData());
                }
            }
        }
        if (draftMeta != null) {
            for (Meta meta : lazyMetaList) {
                meta.getData().put(DraftModule.MODULE_MODULE, draftMeta.getCurrentModuleData());
            }
        }
    }
}
