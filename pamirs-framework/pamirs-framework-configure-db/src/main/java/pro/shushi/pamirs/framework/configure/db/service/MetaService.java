package pro.shushi.pamirs.framework.configure.db.service;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 模块元数据服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MetaService extends CommonApi {

    Map<String/*module*/, MetaData> loadMetaDataMap(Set<String> modules, Consumer<MetaBaseModel> directive, Supplier<Boolean> preAction);

    MetaData loadMetaData(String module, Consumer<MetaBaseModel> directive);

    void crossingLoadMetaData(Map<String, MetaData> metaDataMap);

    void prepareModels(Map<String/*model*/, String/*simulate model*/> modelMap);

    Set<String> sortModelSet(Set<String> modelSet);

    Set<String> sortModelSet(Set<String> modelSet, Map<String, Integer> metaModelPriorityMap);

    HoldKeeper<MetaService> holder = new HoldKeeper<>();

    static MetaService get() {
        return holder.supply(() -> Spider.getDefaultExtension(MetaService.class));
    }
}
