package pro.shushi.pamirs.meta.api.dto.config.api;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 模型配置扩展API
 *
 * @author Adamancy Zhang at 14:16 on 2025-03-31
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModelConfigExtendApi {

    default void getModelFieldConfigList(ModelConfig modelConfig, List<ModelFieldConfig> modelFieldConfigList) {
    }

    default void getModelFieldConfigListSort(ModelConfig modelConfig, List<ModelFieldConfig> modelFieldConfigList) {
    }

    default void getModels(ModelConfig modelConfig, List<String> models) {
    }

    default void getSqlMethodModelFieldConfigList(ModelConfig modelConfig, List<ModelFieldConfig> modelFieldConfigList) {
    }

    HoldKeeper<List<ModelConfigExtendApi>> holder = new HoldKeeper<>();

    static List<ModelConfigExtendApi> get() {
        return holder.supply(() -> Spider.getLoader(ModelConfigExtendApi.class).getOrderedExtensions());
    }
}
