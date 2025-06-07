package pro.shushi.pamirs.boot.common.spi.service.boot;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModuleLifecycleAroundApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 启动模块生命周期环切接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultBootModuleLifecycleAround implements BootModuleLifecycleAroundApi {
}
