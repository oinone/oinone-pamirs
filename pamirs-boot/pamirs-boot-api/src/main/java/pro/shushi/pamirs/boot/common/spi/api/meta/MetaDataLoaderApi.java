package pro.shushi.pamirs.boot.common.spi.api.meta;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 元数据加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MetaDataLoaderApi {

    Map<String/*module*/, Meta> load(AppLifecycleCommand command,
                                     Set<String> includeModules,
                                     Set<String> excludeModules,
                                     Map<String, ModuleDefinition> moduleInfoMap,
                                     Map<String/*module*/, MetaData> updateModuleMap,
                                     Map<String/*module*/, MetaData> reloadModuleMap);

    void loadSessionFromMeta(AppLifecycleCommand command, Set<String> runModules, List<Meta> metaList);

    default List<Meta> filter(List<ModuleDefinition> filterModules, List<Meta> metaList) {
        Set<String> filterSet = filterModules.stream().map(ModuleDefinition::getModule).collect(Collectors.toSet());
        List<Meta> validMetaList = new ArrayList<>();
        for (Meta meta : metaList) {
            String module = meta.getModule();
            if (!filterSet.contains(module)) {
                continue;
            }
            validMetaList.add(meta);
        }
        return validMetaList;
    }

    @SuppressWarnings("unused")
    default List<Meta> merge(List<Meta> firstMetaList, List<Meta> secondMetaList) {
        List<Meta> metaList = new ArrayList<>();
        if (null != firstMetaList) {
            metaList.addAll(firstMetaList);
        }
        if (null != secondMetaList) {
            metaList.addAll(secondMetaList);
        }
        return metaList;
    }

    default void loadSessionModules(AppLifecycleCommand command, Map<String, ModuleDefinition> moduleInfoMap, List<Meta> metaList) {
        if (!command.getOptions().isRefreshSessionMeta()) {
            return;
        }
        for (String module : moduleInfoMap.keySet()) {
            PamirsSession.getContext().addModule(moduleInfoMap.get(module));
        }
    }

}
