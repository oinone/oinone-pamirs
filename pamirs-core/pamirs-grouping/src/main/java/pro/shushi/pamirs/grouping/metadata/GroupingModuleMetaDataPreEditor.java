//package pro.shushi.pamirs.grouping.metadata;
//
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
//import pro.shushi.pamirs.boot.common.extend.MetaDataPreEditor;
//import pro.shushi.pamirs.grouping.GroupingModule;
//import pro.shushi.pamirs.meta.api.dto.meta.Meta;
//import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 分组模块元数据计算预处理
// * <p>
// * 此元数据预处理用于无需显示依赖即可参与跨模块元数据计算的场景
// *
// * @author Adamancy Zhang at 17:58 on 2025-11-25
// */
//@Order(999)
//@Component
//public class GroupingModuleMetaDataPreEditor implements MetaDataPreEditor {
//
//    @Override
//    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
//        List<Meta> lazyMetaList = new ArrayList<>();
//        Meta groupingMeta = null;
//        for (Meta meta : metaMap.values()) {
//            String module = meta.getModule();
//            if (ModuleConstants.MODULE_BASE.equals(module)) {
//                continue;
//            }
//            if (GroupingModule.MODULE_MODULE.equals(module)) {
//                groupingMeta = meta;
//                continue;
//            }
//            if (!meta.getData().containsKey(GroupingModule.MODULE_MODULE)) {
//                if (groupingMeta == null) {
//                    lazyMetaList.add(meta);
//                } else {
//                    meta.getData().put(GroupingModule.MODULE_MODULE, groupingMeta.getCurrentModuleData());
//                }
//            }
//        }
//        if (groupingMeta != null) {
//            for (Meta meta : lazyMetaList) {
//                meta.getData().put(GroupingModule.MODULE_MODULE, groupingMeta.getCurrentModuleData());
//            }
//        }
//    }
//}
