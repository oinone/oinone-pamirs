package pro.shushi.pamirs.framework.compare.core;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.compare.MetaDataLoadExtend;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.ModelData;

/**
 * 元数据加载扩展服务
 * <p>
 * 2020/11/19 10:59 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
public class DefaultMetaDataLoadExtend implements MetaDataLoadExtend {

    @Override
    public <T extends MetaBaseModel> void handle(ModelData modelData, T t) {

    }

}
