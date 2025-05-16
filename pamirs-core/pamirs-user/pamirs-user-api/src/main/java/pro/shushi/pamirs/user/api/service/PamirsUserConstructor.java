package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.user.api.model.PamirsUser;

/**
 * 用户的构造函数
 * @author shier
 * date  2022/7/1 下午5:32
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PamirsUserConstructor {

    PamirsUser construct(PamirsUser user);

}
