package pro.shushi.pamirs.framework.orm.client.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.reentry.ReentryClearApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.common.spi.SPI;

import jakarta.annotation.Resource;

/**
 * 前后端数据转换服务
 * <p>
 * 递归遍历
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/2/20
 */
@SuppressWarnings("unchecked")
@SPI.Service
@Component
public class OrmClientDataConverter implements ClientDataConverter {

    @Resource
    private ReentryClearApi reentryClearApi;

    @Resource
    private DefaultClientDataConverter frontEndDataConverter;

    @Resource
    private RemoteClientDataConverter remoteClientDataConverter;

    @Override
    public <T> T in(ModelComputeContext context, String model, Object obj, String clientType) {
        clear(model, obj, clientType);
        try {
            return getClientDataConverter(clientType).in(context, model, obj);
        } finally {
            clear(model, obj, clientType);
        }
    }

    @Override
    public <T> T out(String model, Object obj, String clientType) {
        clear(model, obj, clientType);
        try {
            T t = getClientDataConverter(clientType).out(model, obj);
            return t;
        } finally {
            clear(model, obj, clientType);
        }
    }

    private void clear(String model, Object obj, String clientType) {
        getClientDataConverter(clientType).clear();
        reentryClearApi.clear(model, obj);
    }

    private ClientDataConverter getClientDataConverter(String clientType) {
        //默认前端请求
        if (StringUtils.isBlank(clientType)) {
            return frontEndDataConverter;
        }
        if (ClientDataConverter.CLIENT_TYPE_FRONTEND.equals(clientType)) {
            return frontEndDataConverter;

        } else if (ClientDataConverter.CLIENT_TYPE_RPC.equals(clientType)) {
            return remoteClientDataConverter;
        }
        return frontEndDataConverter;
    }
}
