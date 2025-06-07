package pro.shushi.pamirs.meta.api.core.configure.annotation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 元模型反射签名器
 *
 * @param <T> T为元模型类
 * @param <D> D为Java Class、Method等Java meta类型
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SuppressWarnings("unused")
@SPI
public interface ModelReflectSigner<T, D> extends CommonApi {

    /**
     * 从配置获取元模型数据签名
     *
     * @param names  命名
     * @param source 源
     * @return 返回值
     */
    String sign(MetaNames names, D source);

}
