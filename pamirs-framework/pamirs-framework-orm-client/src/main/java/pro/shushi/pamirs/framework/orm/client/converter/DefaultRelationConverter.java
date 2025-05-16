package pro.shushi.pamirs.framework.orm.client.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientRelationConverter;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;

/**
 * 前后端关系转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SPI.Service
@Component
public class DefaultRelationConverter implements ClientRelationConverter {

    @Resource
    private ClientDataConverter defaultClientDataConverter;

    @Override
    public Object resultHandler(String model, Object result) {
        // 前后端字段适配
        result = defaultClientDataConverter.out(model, result);
        return result;
    }
}
