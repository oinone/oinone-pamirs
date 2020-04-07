package pro.shushi.pamirs.meta.api.core.configure.annotation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;

/**
 * 元模型签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 * @param <T> T为元模型类
 * @param <D> D为Java Class、Method等Java meta类型
 *
 */
public interface ModelSigner<T, D> extends CommonApi {

    /**
     * 从配置获取元模型数据签名
     *
     * @param names
     * @param source
     * @return
     */
    String sign(MetaNames names, D source);

    /**
     * 从元模型对象获取元模型数据签名
     *
     * @param metaModelObject
     * @return
     */
    String sign(T metaModelObject);

}
