package pro.shushi.pamirs.boot.web.loader.deprecated;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.utils.PageLoadHelper;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用于前端的元数据加载
 *
 * @author shier
 * date  2020/11/30 11:30 上午
 */
@Slf4j
@Base
@Component
@pro.shushi.pamirs.meta.annotation.Fun(UeModule.MODEL_MODEL)
public class ModuleLoadAction extends AbstractLoadAction {

    @Resource
    private MetaCacheManager metaCacheManager;

    @Resource
    private UiIoManager uiIoManager;

    @Base
    @Function(summary = "提供给前端加载module的元数据", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "获取模块元数据", type = FunctionTypeEnum.QUERY)
    public UeModule load(UeModule module) {
        //加载模块
        long start1 = System.currentTimeMillis();
        module = loadModule(module);
        log.info("Load module cost: [" + (System.currentTimeMillis() - start1) + "] ms ..");
        long start2 = System.currentTimeMillis();
        loadMenu(module);
        log.info("Load menu cost: [" + (System.currentTimeMillis() - start2) + "] ms ..");
        return module;
    }

    private UeModule loadModule(UeModule module) {
        String moduleName = module.getName();
        if (StringUtils.isBlank(moduleName)) {
            log.error("Load metadata query condition error, requested module name field is empty");
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_LOAD_MODULE_META_NO_MODULE_NAME_ERROR).errThrow();
        }
        UeModule returnModule;
        UeModule dbModule = new UeModule().queryOneByWrapper(Pops.<UeModule>lambdaQuery()
                .from(UeModule.MODEL_MODEL)
                .eq(UeModule::getName, moduleName));
        if (dbModule == null) {
            ModuleDefinition cacheModule = PamirsSession.getContext().getModuleCache().getByName(module.getName());
            if (cacheModule == null) {
                throw PamirsException.construct(BootUxdExpEnumerate.BASE_LOAD_MODULE_META_MODULE_DATA_ERROR).errThrow();
            }
            ModuleDefinition cloneModule = BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(cacheModule);
            returnModule = ArgUtils.convert(ModuleDefinition.MODEL_MODEL, ModuleDefinition.UE_MODEL_MODEL, cloneModule);
        } else {
            returnModule = dbModule;
        }
        if (AuthApi.get().canAccessModule(returnModule.getModule()).getSuccess()) {
            returnModule.setHomePage(null);
            return returnModule;
        }
        throw PamirsException.construct(BootUxdExpEnumerate.BASE_MODULE_CAN_NOT_ACCESS_ERROR)
                .appendMsg(I18nUtils.getMessage("pamirs.boot.ui.auth.moduleAccessDenied.module", returnModule.getDisplayName(), returnModule.getModule()))
                .errThrow();
    }

    private void loadMenu(UeModule moduleDefinition) {
        String module = moduleDefinition.getModule();
        List<Menu> menus = PageLoadHelper.fetchValidMenus(module);
        if (CollectionUtils.isEmpty(menus)) {
            moduleDefinition.setAllMenus(null);
            return;
        }
        for (Menu menu : menus) {
            menu.setParentName(menu.getParentName());
            String actionModel = menu.getModel();
            String actionName = menu.getActionName();
            if (StringUtils.isAnyBlank(actionModel, actionName)) {
                continue;
            }
            menu.setModuleDefinition(null);
            Action action = metaCacheManager.fetchAction(actionModel, actionName);
            if (action == null) {
                log.error("Action cache loss. model = {}, name = {}", actionModel, actionName);
                continue;
            }
            if (action instanceof ViewAction) {
                menu.setViewAction(PageLoadHelper.fillModuleAndModel(uiIoManager.cloneData((ViewAction) action)));
                menu.setServerAction(null);
                menu.setUrlAction(null);
                menu.setClientAction(null);
            } else if (action instanceof ServerAction) {
                menu.setServerAction(PageLoadHelper.fillModuleAndModel(uiIoManager.cloneData((ServerAction) action)));
                menu.setViewAction(null);
                menu.setUrlAction(null);
                menu.setClientAction(null);
            } else if (action instanceof UrlAction) {
                menu.setUrlAction(PageLoadHelper.fillModuleAndModel(uiIoManager.cloneData((UrlAction) action)));
                menu.setServerAction(null);
                menu.setViewAction(null);
                menu.setClientAction(null);
            } else if (action instanceof ClientAction) {
                menu.setClientAction(PageLoadHelper.fillModuleAndModel(uiIoManager.cloneData((ClientAction) action)));
                menu.setServerAction(null);
                menu.setViewAction(null);
                menu.setUrlAction(null);
            } else {
                log.error("Invalid action cache. model = {}, actionName = {}", actionModel, actionName);
            }
        }
        moduleDefinition.setAllMenus(menus);
    }
}
