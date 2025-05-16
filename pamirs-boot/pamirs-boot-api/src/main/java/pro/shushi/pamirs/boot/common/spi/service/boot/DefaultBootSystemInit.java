package pro.shushi.pamirs.boot.common.spi.service.boot;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootSystemInitApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 启动系统初始化接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service
public class DefaultBootSystemInit implements BootSystemInitApi {

}
