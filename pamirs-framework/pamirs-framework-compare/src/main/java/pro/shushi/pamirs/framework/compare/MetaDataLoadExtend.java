package pro.shushi.pamirs.framework.compare;

import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.ModelData;

/**
 * 元数据加载扩展接口
 * <p>
 * 2020/11/19 10:59 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface MetaDataLoadExtend {

    /**
     * 扩展处理
     *
     * @param modelData 元数据注册信息
     * @param t         元数据
     * @param <T>       元数据类型
     */
    <T extends MetaBaseModel> void handle(ModelData modelData, T t);

}
