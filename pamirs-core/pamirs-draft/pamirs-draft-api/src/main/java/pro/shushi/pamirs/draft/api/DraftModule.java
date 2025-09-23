//package pro.shushi.pamirs.draft.api;
//
//import org.springframework.stereotype.Component;
//import pro.shushi.pamirs.core.common.CommonModule;
//import pro.shushi.pamirs.meta.annotation.Module;
//import pro.shushi.pamirs.meta.base.PamirsModule;
//import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
//
///**
// * 草稿模块
// *
// * @author Gesi at 15:30 on 2025/9/17
// */
//@Component
//@Module(
//        name = DraftModule.MODULE_NAME,
//        displayName = "草稿",
//        version = "5.0.0",
//        dependencies = {
//                ModuleConstants.MODULE_BASE,
//                CommonModule.MODULE_MODULE
//        }
//)
//@Module.module(DraftModule.MODULE_MODULE)
//@Module.Advanced(selfBuilt = true, application = false)
//public class DraftModule implements PamirsModule {
//
//    public static final String MODULE_MODULE = "draft";
//
//    public static final String MODULE_NAME = "draft";
//
//    @Override
//    public String[] packagePrefix() {
//        return new String[]{
//                "pro.shushi.pamirs.draft",
//        };
//    }
//
//}
