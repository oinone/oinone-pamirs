package pro.shushi.pamirs.translate.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ErrorDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.translate.constant.TranslateConstants;
import pro.shushi.pamirs.translate.proxy.TranslationItemExportProxy;
import pro.shushi.pamirs.translate.service.TranslateCompileContext;
import pro.shushi.pamirs.translate.service.TranslationDslNodeVisitor;
import pro.shushi.pamirs.translate.visitor.DslParser;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.translate.constant.TranslateConstants.MODULES_TO_EXCLUDE;

@Slf4j
public class UniversalParser {

    /**
     * 解析模型
     *
     * @param moduleNameMap
     * @param tContext
     * @param module
     */
    public static void parseModel(Map<String, String> moduleNameMap, Map<String, Set<String>> tContext, String module) {
        LambdaQueryWrapper<ModelDefinition> qw = Pops.<ModelDefinition>lambdaQuery().from(ModelDefinition.MODEL_MODEL);
        if (StringUtils.isNotBlank(module)) {
            if (MODULES_TO_EXCLUDE.contains(module)) {
                return;
            }
            qw.eq(StringUtils.isNotBlank(module), ModelDefinition::getModule, module);
        } else {
            qw.notIn(ModelDefinition::getModule, MODULES_TO_EXCLUDE);
        }
        List<ModelDefinition> modelDefinitions = new ModelDefinition().queryList(qw);
        if (CollectionUtils.isNotEmpty(modelDefinitions)) {
            for (ModelDefinition modelDefinition : modelDefinitions) {
                if (moduleNameMap.containsKey(modelDefinition.getModule())) {
                    tContext.computeIfAbsent(modelDefinition.getModule(), k -> new HashSet<>()).add(modelDefinition.getDisplayName());
                }
            }
        }
    }

    /**
     * 解析枚举
     *
     * @param moduleNameMap
     * @param tContext
     * @param module
     */
    public static void parseModelEnum(Map<String, String> moduleNameMap, Map<String, Set<String>> tContext, String module) {
        LambdaQueryWrapper<DataDictionary> qw = Pops.<DataDictionary>lambdaQuery().from(DataDictionary.MODEL_MODEL);
        if (StringUtils.isNotBlank(module)) {
            if (MODULES_TO_EXCLUDE.contains(module)) {
                return;
            }
            qw.eq(StringUtils.isNotBlank(module), DataDictionary::getModule, module);
        } else {
            qw.notIn(DataDictionary::getModule, MODULES_TO_EXCLUDE);
        }
        List<DataDictionary> dataDictionaries = new DataDictionary().queryList(qw);
        if (CollectionUtils.isNotEmpty(dataDictionaries)) {
            for (DataDictionary dataDictionary : dataDictionaries) {
                String dModule = dataDictionary.getModule();
                if (moduleNameMap.containsKey(dModule)) {
                    List<DataDictionaryItem> dictionaryItems = dataDictionary.getOptions();
                    if (CollectionUtils.isNotEmpty(dictionaryItems)) {
                        for (DataDictionaryItem dictionaryItem : dictionaryItems) {
                            tContext.computeIfAbsent(dModule, k -> new HashSet<>()).add(dictionaryItem.getDisplayName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 解析错误信息
     *
     * @param moduleNameMap
     * @param tContext
     * @param module
     */
    //select * from base_error_definition where id in (select res_id from base_model_data where model = 'base.ErrorDefinition' and module = 'eip_designer' and is_deleted = 0) and is_deleted = 0;
    public static void parseErrorInfo(Map<String, String> moduleNameMap, Map<String, Set<String>> tContext, String module) {
        LambdaQueryWrapper<ModelData> queryWrapper = Pops.<ModelData>lambdaQuery().from(ModelData.MODEL_MODEL);
        queryWrapper.eq(ModelData::getModel, TranslateConstants.ERRORDEFINITION_MODEL);
        if (StringUtils.isNotBlank(module)) {
            if (MODULES_TO_EXCLUDE.contains(module)) {
                return;
            }
            queryWrapper.eq(StringUtils.isNotBlank(module), ModelData::getModule, module);
        } else {
            queryWrapper.notIn(ModelData::getModule, MODULES_TO_EXCLUDE);
        }
        List<ModelData> modelData = new ModelData().queryList(queryWrapper);
        if (CollectionUtils.isNotEmpty(modelData)) {
            Map<Long, String> modelDataMap = modelData.stream().collect(Collectors.toMap(ModelData::getResId, ModelData::getModule));

            List<ErrorDefinition> errorDefinitions = new ErrorDefinition().queryList(Pops.<ErrorDefinition>lambdaQuery().from(ErrorDefinition.MODEL_MODEL).in(ErrorDefinition::getId, modelDataMap.keySet()));

            errorDefinitions.forEach(errorDefinition -> {
                String errorModule = modelDataMap.get(errorDefinition.getId());
                if (moduleNameMap.containsKey(errorModule)) {
                    tContext.computeIfAbsent(modelDataMap.get(errorDefinition.getId()), k -> new HashSet<>()).add(errorDefinition.getMsg());
                }
            });
        }
    }

    /**
     * 解析XML 模型
     *
     * @param data 过滤条件
     * @return
     */
    public static TranslationDslNodeVisitor getTranslationDslNodeVisitor(TranslationItemExportProxy data) {
        Map<String, Set<String>> map = new HashMap<>();
        TranslationDslNodeVisitor translationDslNodeVisitor = new TranslationDslNodeVisitor(map);
        List<UeModule> moduleDefinitions = new UeModule().queryList(Pops.<UeModule>lambdaQuery().from(UeModule.MODEL_MODEL).eq(StringUtils.isNotBlank(data.getModule()), UeModule::getModule, data.getModule()).eq(UeModule::getApplication, true));
        if (CollectionUtils.isNotEmpty(moduleDefinitions)) {
            moduleDefinitions.forEach(v -> {
                v.setHomePageModel(v.getHomePageModel());
                v.setHomePageName(v.getHomePageName());
            });
            moduleDefinitions = new UeModule().listFieldQuery(moduleDefinitions, UeModule::getHomePage);
        }
        for (UeModule ueModule : moduleDefinitions) {
            //对界面设计器进行内置处理
            if (MODULES_TO_EXCLUDE.contains(ueModule.getModule())) {
                continue;
            }
            if (StringUtils.isNotBlank(ueModule.getDisplayName())) {
                translationDslNodeVisitor.context.computeIfAbsent(ueModule.getModule(), k -> new HashSet<>()).add(ueModule.getDisplayName());
            }
            ViewAction homePage = ueModule.getHomePage();
            if (homePage != null && Optional.ofNullable(PamirsSession.getContext().getModule(homePage.getModule())).map(ModuleDefinition::getApplication).orElse(false)) {
                String displayName = homePage.getDisplayName();
                if (StringUtils.isNotBlank(displayName)) {
                    translationDslNodeVisitor.context.computeIfAbsent(homePage.getModule(), k -> new HashSet<>()).add(displayName);
                }
                String title = homePage.getTitle();
                if (StringUtils.isNotBlank(title)) {
                    translationDslNodeVisitor.context.computeIfAbsent(homePage.getModule(), k -> new HashSet<>()).add(title);
                }
                try {
                    View view = fetchMainView(homePage);
                    UIView uiView = DslParser.parser(view);
                    TranslateCompileContext context = new TranslateCompileContext(homePage.getModule());
                    translationDslNodeVisitor.setCurrentContext(context);
                    DslParser.visit(uiView, translationDslNodeVisitor);
                } catch (Exception e) {
                    log.error("Parse XML file model: {}", homePage.getModel(), e);
                }
            }

            List<Menu> menus = new Menu().queryList(Pops.<Menu>lambdaQuery().from(Menu.MODEL_MODEL).eq(Menu::getModule, ueModule.getModule()));
            if (CollectionUtils.isEmpty(menus)) {
                continue;
            }
            menus = new Menu().listFieldQuery(menus, Menu::getViewAction);
            for (Menu menu : menus) {
                String displayName = menu.getDisplayName();
                if (StringUtils.isNotBlank(displayName)) {
                    translationDslNodeVisitor.context.computeIfAbsent(menu.getModule(), k -> new HashSet<>()).add(displayName);
                }
                ViewAction viewAction = menu.getViewAction();
                if (viewAction == null) {
                    continue;
                }
                String title = viewAction.getTitle();
                if (StringUtils.isNotBlank(title)) {
                    translationDslNodeVisitor.context.computeIfAbsent(viewAction.getModule(), k -> new HashSet<>()).add(title);
                }
                try {
                    View view = fetchMainView(viewAction);
                    UIView uiView = DslParser.parser(view);
                    TranslateCompileContext context = new TranslateCompileContext(menu.getModule());
                    translationDslNodeVisitor.setCurrentContext(context);
                    DslParser.visit(uiView, translationDslNodeVisitor);
                } catch (Exception e) {
                    log.error("Parse XML file model: {} ", menu.getModel(), e);
                }
            }
        }
        if (StringUtils.isBlank(data.getModule()) || TranslateConstants.APPS.equals(data.getModule())) {
            List<ModuleCategory> moduleCategorys = new ModuleCategory().queryList();
            List<ModuleDefinition> moduleDefinitionList = new ModuleDefinition().queryList();
            if (CollectionUtils.isNotEmpty(moduleCategorys)) {
                Set<String> moduleCategoryNames = moduleCategorys.stream().map(ModuleCategory::getName).collect(Collectors.toSet());
                translationDslNodeVisitor.context.computeIfAbsent(TranslateConstants.APPS, k -> new HashSet<>()).addAll(moduleCategoryNames);
            }
            if (CollectionUtils.isNotEmpty(moduleDefinitionList)) {
                Set<String> moduleDisplayNames = moduleDefinitionList.stream().map(ModuleDefinition::getDisplayName).collect(Collectors.toSet());
                translationDslNodeVisitor.context.computeIfAbsent(TranslateConstants.APPS, k -> new HashSet<>()).addAll(moduleDisplayNames);
            }
        }
        return translationDslNodeVisitor;
    }

    protected static View fetchMainView(ViewAction viewAction) {
        MetaCacheManager metaCacheManager = BeanDefinitionUtils.getBean(MetaCacheManager.class);
        String resModel = Optional.ofNullable(viewAction.getResModel()).orElse(viewAction.getModel());
        View resView = viewAction.getResView();
        View mainView;
        if (resView == null || resView.getTemplate() == null) {
            mainView = metaCacheManager.fetchView(resModel, viewAction.getResViewName(), viewAction.getViewType());
        } else {
            mainView = resView;
        }
        return mainView;
    }

}