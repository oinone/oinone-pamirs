package pro.shushi.pamirs.eip.api.processor;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipProcessor;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.io.Serializable;

/**
 * @author Adamancy Zhang at 15:04 on 2021-02-24
 */
@Slf4j
public abstract class AbstractProcessor<T extends IEipApi> implements IEipProcessor<T> {

    private final T api;

    protected AbstractProcessor(T api) {
        this.api = api;
    }

    @Override
    public T getApi() {
        return api;
    }

    /**
     * 更换默认process方法的入参，不可修改
     *
     * @param exchange 交换信息
     * @throws Exception 任何异常
     */
    @Override
    public final void process(Exchange exchange) throws Exception {
        if (useEipUserAuthentication()) {
            PamirsSession.clear();
            setDefaultUser();
            processor((ExtendedExchange) exchange);
        } else {
            processor((ExtendedExchange) exchange);
        }
    }

    /**
     * 是否使用eip用户认证
     *
     * @return 是否使用eip用户认证 default value {@link Boolean#FALSE}
     */
    protected boolean useEipUserAuthentication() {
        return false;
    }

    /**
     * 处理过程
     *
     * @param exchange 交换信息
     * @throws Exception 任何异常
     */
    public abstract void processor(ExtendedExchange exchange) throws Exception;

    private void setDefaultUser() {
        Serializable userId = PamirsSession.getUserId();
        if (userId != null) {
            log.error("eip open interface user id is exist. id={}", userId, new RuntimeException());
        }
        PamirsSession.setUserId(EipConfigurationConstant.EIP_SYSTEM_USER_ID);
        PamirsSession.setUserName(I18nUtils.getMessage(EipConfigurationConstant.EIP_SYSTEM_USER_NAME));
    }
}
