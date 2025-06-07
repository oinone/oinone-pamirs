package pro.shushi.pamirs.meta.api.core.configure;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 获取元模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface MetaModelFetcher extends CommonApi {

    /**
     * 获取元模型
     *
     * @return 返回元模型列表
     */
    List<MetaModel> fetchMetaModelList();

    /**
     * 获取元模型编码
     *
     * @return 返回元模型编码列表
     */
    List<String/*model*/> fetchMetaModels();

    /**
     * 根据元模型所在包获取所有类
     *
     * @return 类集合
     */
    Set<Class<?>> fetchMetaClasses();

    /**
     * 获取元模型优先级
     *
     * @return 元模型优先级map
     */
    Map<String, Integer> fetchMetaModelPriorityMap();

}
