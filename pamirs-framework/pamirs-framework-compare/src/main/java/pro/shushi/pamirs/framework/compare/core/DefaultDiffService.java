package pro.shushi.pamirs.framework.compare.core;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.compare.DiffService;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.DiffUtils;

/**
 * 差量服务
 * <p>
 * 2020/11/19 10:59 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
public class DefaultDiffService implements DiffService {

    @Override
    public <T extends MetaBaseModel> void hash(T t) {
        t.setStringify(t.stringify());
        t.setHash(t.hashSum(t.getStringify()));
    }

    @Override
    public <T extends MetaBaseModel> boolean diff(T t) {
        return DiffUtils.diff(t);
    }

}
