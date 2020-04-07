package pro.shushi.pamirs.meta.api.core.configure;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * 获取模型元数据
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface ModelModelFetcher extends CommonApi {

    /**
     * 通过modelClazz获取模型编码
     *
     * @param modelClazz
     * @param <T>
     * @return
     */
    <T> String getModel(Class<T> modelClazz);

    /**
     * 通过对象获取模型
     *
     * @param modelObject
     * @param <T>
     * @return
     */
    <T> String getModel(T modelObject);

    /**
     * 通过对象获取命名空间
     *
     * @param modelObject
     * @param <T>
     * @return
     */
    <T> String getNamespace(T modelObject);

}
