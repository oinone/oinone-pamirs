package pro.shushi.pamirs.meta.api.dto.config.api;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 模型配置扩展接口
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/04
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModelConfigApi {

    /**
     * 获取模型的ModelFieldConfig列表
     *
     * @param modelConfig
     * @return
     */
    List<ModelFieldConfig> getModelFieldConfigList(ModelConfig modelConfig);

    /**
     * 获取模型的ModelFieldConfig列表 带排序
     *
     * @param modelConfig
     * @return
     */
    List<ModelFieldConfig> getModelFieldConfigListSort(ModelConfig modelConfig);

    /**
     * 根据Model对应的Table获取model编码列表
     *
     * @return
     */
    List<String> getModels(ModelConfig modelConfig);

    /**
     * 获取model对应table包含的所有的模型的字段列表，字段列表只包含(store=true).
     * 适用于：拼接SQL查询, 只返回存储字段
     *
     * @return
     */
    List<ModelFieldConfig> getSqlMethodModelFieldConfigList(ModelConfig modelConfig);

    HoldKeeper<ModelConfigApi> holder = new HoldKeeper<>();

    static ModelConfigApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(ModelConfigApi.class));
    }
}
