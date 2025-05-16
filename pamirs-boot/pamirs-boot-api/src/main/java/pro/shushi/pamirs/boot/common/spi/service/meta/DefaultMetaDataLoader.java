package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataLoaderApi;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataPreEditApi;
import pro.shushi.pamirs.framework.common.api.PlatformJarVersionCheckerApi;
import pro.shushi.pamirs.framework.compare.CrossingExtendService;
import pro.shushi.pamirs.framework.compute.InheritedComputeTemplate;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.framework.configure.annotation.core.AnnotationConfigurer;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.session.Sessions;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionCacheFactoryApi;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import javax.annotation.Resource;
import java.util.*;

import static pro.shushi.pamirs.boot.common.enmu.BootExpEnumerate.BASE_BOOT_LOAD_META_FROM_ANNOTATION_ERROR;

/**
 * 元数据加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
@SPI.Service
public class DefaultMetaDataLoader implements MetaDataLoaderApi {

    @Resource
    private AnnotationConfigurer annotationConfigurer;

    @Resource
    private CrossingExtendService crossingExtendService;

    @Resource
    private MetaConfiguration metaConfiguration;

    @Resource
    private SessionCacheFactoryApi defaultSessionCacheFactoryApi;

    @Resource
    private PlatformJarVersionCheckerApi platformJarVersionCheckerApi;

    @Override
    public Map<String/*module*/, Meta> load(AppLifecycleCommand command,
                                            Set<String> includeModules,
                                            Set<String> excludeModules,
                                            Map<String, ModuleDefinition> moduleInfoMap,
                                            Map<String/*module*/, MetaData> updateModuleMap,
                                            Map<String/*module*/, MetaData> reloadModuleMap) {
        // 扫描元数据
        if (CollectionUtils.isEmpty(includeModules)) {
            return new LinkedHashMap<>();
        }
        platformJarVersionCheckerApi.init(command.getOptions().isGoBack());
        Result<List<Meta>> metaDataResult = TimeWatcher.watch(() ->
                        annotationConfigurer.extractDefinition(command.getOptions().isLoadMeta(),
                                includeModules, excludeModules, moduleInfoMap,
                                updateModuleMap, reloadModuleMap),
                "扫描元数据全部完成.");
        platformJarVersionCheckerApi.compare();
        if (!metaDataResult.isSuccess()) {
            metaDataResult.logMessages(metaConfiguration.getLogLevel());
            throw PamirsException.construct(BASE_BOOT_LOAD_META_FROM_ANNOTATION_ERROR).errThrow();
        }

        List<Meta> metaList = metaDataResult.getData();
        if (CollectionUtils.isEmpty(metaList)) {
            return new LinkedHashMap<>();
        }

        Map<String/*module*/, Meta> metaMap = new LinkedHashMap<>();
        for (Meta meta : metaList) {
            metaMap.put(meta.getModule(), meta);
        }

        // 编程式预处理待计算元数据
        Spider.getDefaultExtension(MetaDataPreEditApi.class).edit(command, metaMap);

        // base元数据继承和关系的差量加载
        // 将直接继承而来的元数据定义字段标记为元数据需要变更，后续元数据计算会重新计算继承链中的所有继承字段
        // 否则在差量安装时，系统会重复安装如id、create_date等字段
        resolveBaseMeta(metaMap.get(ModuleConstants.MODULE_BASE));

        // 差量计算：跨模块字段和方法挂载处理
        crossingExtendService.diffCompute(metaList);

        return metaMap;
    }

    @Override
    public void loadSessionFromMeta(AppLifecycleCommand command, Set<String> runModules, List<Meta> metaList) {
        if (!command.getOptions().isRefreshSessionMeta()) {
            return;
        }
        List<MetaData> metaDataList = new ArrayList<>();
        Set<String> collectedModuleSet = new HashSet<>();
        for (Meta meta : metaList) {
            for (String module : meta.getData().keySet()) {
                if (!collectedModuleSet.contains(module)) {
                    metaDataList.add(meta.getData().get(module));
                }
                collectedModuleSet.add(module);
            }
        }
        RequestContext requestContext = RequestContext.newContext(defaultSessionCacheFactoryApi);
        Sessions.fillSession(requestContext, metaDataList, Boolean.TRUE, command.getOptions().isLoadMeta());
    }

    private void resolveBaseMeta(Meta baseMeta) {
        for (String model : new String[]{
                ModelDefinition.MODEL_MODEL,
                ModelField.MODEL_MODEL,
                ModuleDefinition.MODEL_MODEL,
                FunctionDefinition.MODEL_MODEL
        }) {
            ModelDefinition modelDefinition = baseMeta.getModel(model);
            for (ModelField modelField : modelDefinition.getModelFields()) {
                if (SystemSourceEnum.isInherited(modelField.getSystemSource())) {
                    InheritedComputeTemplate.compute(baseMeta, modelDefinition, new HashMap<>(),
                            null, null, true, null,
                            (currentModel, superModel) -> disableMetaCompleted(superModel, modelField), null,
                            (currentModel, superModel) -> disableMetaCompleted(superModel, modelField), null,
                            (currentModel, superModel) -> disableMetaCompleted(superModel, modelField), null);
                }
            }
        }
    }

    private void disableMetaCompleted(ModelDefinition superModel, ModelField field) {
        if (CollectionUtils.isEmpty(superModel.getModelFields())) {
            return;
        }
        for (ModelField superField : superModel.getModelFields()) {
            if (field.getField().equals(superField.getField())) {
                if (!SystemSourceEnum.isInherited(superField.getSystemSource())
                        && !superField.isMetaCompleted()) {
                    // 元数据定义有效继承，预先标记为需要重新计算
                    Models.modelDirective().disableMetaCompleted(field);
                    // 标记为继承元数据
                    Models.modelDirective().enableMetaInherited(field);
                }
                break;
            }
        }
    }

    @Override
    public void loadSessionModules(AppLifecycleCommand command, Map<String, ModuleDefinition> moduleInfoMap, List<Meta> metaList) {
        if (!command.getOptions().isRefreshSessionMeta()) {
            return;
        }

        for (Meta meta : metaList) {
            String module = meta.getModule();
            if (moduleInfoMap.containsKey(module)) {
                String moduleName = meta.getModule();
                Optional<Map<String, Map<String, MetaBaseModel>>> moduleOpl = Optional.ofNullable(meta.getData().get(moduleName).getData());
                Map<String, Map<String, MetaBaseModel>> moduleMetaData = moduleOpl.orElse(Collections.emptyMap());
                if (CollectionUtils.isEmpty(moduleMetaData)) {
                    log.warn("module:[{}] meta is empty", module);
                    continue;
                }
                Object moduleObj = moduleOpl.map(_map -> _map.get(ModuleDefinition.MODEL_MODEL))
                        .map(Map::values)
                        .map(Collection::iterator)
                        .filter(Iterator::hasNext)
                        .map(Iterator::next)
                        .orElse(null);
                ModuleDefinition moduleDefinition = (ModuleDefinition) moduleObj;
                PamirsSession.getContext().addModule(moduleDefinition);
            }
        }
    }
}
