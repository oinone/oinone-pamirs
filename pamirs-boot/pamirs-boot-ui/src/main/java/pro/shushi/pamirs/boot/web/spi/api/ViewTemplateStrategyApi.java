package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.boot.web.spi.domain.RegisterSearchWidget;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

/**
 * 实现默认搜索栏字段策略
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ViewTemplateStrategyApi {

    /**
     * 实现默认搜索栏字段策略. 返回null，则该字段不作为搜索字段
     *
     * @param modelDefinition
     * @param modelField
     * @return
     */
    RegisterSearchWidget computeSearchWidget(ModelDefinition modelDefinition, ModelField modelField);

}
