package pro.shushi.pamirs.user.api.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.constants.UserConstants;

/**
 * 多用户缓存实现
 *
 * @author Adamancy Zhang at 18:26 on 2024-06-15
 */
@Order
@Component
@SPI.Service(UserConstants.MULTIPLE_USER_CACHE_MODE)
public class MultipleUserCache extends DefaultUserCache {
}
