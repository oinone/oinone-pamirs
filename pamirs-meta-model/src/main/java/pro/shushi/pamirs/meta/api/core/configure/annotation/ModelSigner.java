package pro.shushi.pamirs.meta.api.core.configure.annotation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 元模型签名器
 *
 * @param <T> T为元模型类
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI
public interface ModelSigner<T> extends CommonApi {

    /**
     * 从元模型对象获取元模型数据签名
     *
     * @param metaModelObject 元数据对象
     * @return 返回值
     */
    String sign(T metaModelObject);

}
