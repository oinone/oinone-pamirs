package pro.shushi.pamirs.meta.api.core.compute.systems.inherit;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

/**
 * 继承扩展处理系统
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface InheritedExtendProcessor extends CommonApi {

    /**
     * 抽象基类继承处理逻辑
     *
     * @param meta            元数据
     * @param modelDefinition 当前模型
     * @param superModel      父模型
     */
    default void abstractInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
    }

    /**
     * 临时继承处理逻辑
     *
     * @param meta            元数据
     * @param modelDefinition 当前模型
     * @param superModel      父模型
     */
    default void transientInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
    }

    /**
     * 多表继承处理逻辑
     *
     * @param meta            元数据
     * @param modelDefinition 当前模型
     * @param superModel      父模型
     */
    default void multiTableInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
    }

    /**
     * 扩展继承处理逻辑
     *
     * @param meta            元数据
     * @param modelDefinition 当前模型
     * @param superModel      父模型
     */
    default void extend(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
    }

    /**
     * 代理继承处理逻辑
     *
     * @param meta            元数据
     * @param modelDefinition 当前模型
     * @param superModel      父模型
     */
    default void proxyExtend(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
    }

}
