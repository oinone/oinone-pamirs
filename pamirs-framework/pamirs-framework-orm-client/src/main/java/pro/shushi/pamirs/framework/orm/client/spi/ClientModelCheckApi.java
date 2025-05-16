package pro.shushi.pamirs.framework.orm.client.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.client.checker.ClientCheckService;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelCheckApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;

/**
 * 模型校验接口默认实现
 * <p>
 * 2020/7/1 8:37 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(99)
@Component
@SPI.Service
public class ClientModelCheckApi implements ModelCheckApi {

    @Resource
    private ClientCheckService clientCheckService;

    @Override
    public <T> T checkRequest(String model, T obj) {
        return clientCheckService.check(model, obj);
    }

    @Override
    public <T> T checkRequest(String model, String argName, T obj) {
        return clientCheckService.check(model, argName, obj);
    }

}
