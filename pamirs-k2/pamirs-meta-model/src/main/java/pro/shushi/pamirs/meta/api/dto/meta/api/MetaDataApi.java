package pro.shushi.pamirs.meta.api.dto.meta.api;

import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.Map;

/**
 * 元数据操作接口
 * <p>
 * 2022/3/2 2:12 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface MetaDataApi {

    /**
     * 添加模型元数据
     *
     * @param data 元数据
     * @param item 模型元数据
     */
    default void whenAddModelField(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data, ModelField item) {

    }

    /**
     * 添加函数元数据
     *
     * @param data 元数据
     * @param item 函数元数据
     */
    default void whenAddFunction(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data, FunctionDefinition item) {

    }

    /**
     * 添加非模型和函数的元数据扩展逻辑
     *
     * @param data  元数据
     * @param group 元数据分组
     * @param sign  元数据签名
     * @param item  元数据
     * @param <T>   元数据类型
     */
    default <T extends MetaBaseModel> void whenAddDataItem(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data, String group, String sign, T item) {

    }

    /**
     * 移除函数元数据
     *
     * @param data      元数据
     * @param namespace 函数命名空间
     * @param fun       函数编码
     * @return 移除结果
     */
    default boolean whenRemoveFunction(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data, String namespace, String fun) {
        return true;
    }

    /**
     * 移除字段元数据
     *
     * @param data  元数据
     * @param model 模型编码
     * @param field 字段编码
     * @return 移除结果
     */
    default boolean whenRemoveModelField(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data, String model, String field) {
        return true;
    }

    /**
     * 移除非字段和函数元数据
     *
     * @param data  元数据
     * @param group 元数据分组
     * @param sign  元数据签名
     * @return 移除结果
     */
    default boolean whenRemoveDataItem(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data, String group, String sign) {
        return true;
    }

}
