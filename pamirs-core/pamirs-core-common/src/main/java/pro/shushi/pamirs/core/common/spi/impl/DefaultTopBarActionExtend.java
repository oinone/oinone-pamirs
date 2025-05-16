package pro.shushi.pamirs.core.common.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.spi.TopBarActionExtendApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认顶部栏动作扩展
 *
 * @author Adamancy Zhang at 20:25 on 2024-02-28
 */
@Order
@Component
@SPI.Service
public class DefaultTopBarActionExtend implements TopBarActionExtendApi {
}
