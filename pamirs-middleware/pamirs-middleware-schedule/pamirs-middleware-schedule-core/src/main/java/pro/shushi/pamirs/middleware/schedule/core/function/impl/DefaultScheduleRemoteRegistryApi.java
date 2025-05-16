package pro.shushi.pamirs.middleware.schedule.core.function.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.core.function.ScheduleRemoteRegistryApi;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * 远程调用注册Api,默认实现
 * @author cpc
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultScheduleRemoteRegistryApi implements ScheduleRemoteRegistryApi {

    @Value("${pamirs.framework.system.isolation-key:}")
    private String isolationKey;

    @Override
    public GenericService registryConsumer(FunctionDefinition<?> functionDefinition) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(functionDefinition.getInterfaceName());
        reference.setGeneric(Boolean.TRUE.toString());
        // FIXME: 2022/4/18 pro.shushi.pamirs.distribution.faas.remote.spi.service.RemoteGroupPrefixManager#handle
        String group = functionDefinition.getGroup();
        group = StringUtils.isBlank(isolationKey) ? group : isolationKey + CharacterConstants.SEPARATOR_UNDERLINE + group;
        reference.setGroup(group);
        reference.setTimeout(functionDefinition.getTimeout());
        reference.setVersion(functionDefinition.getVersion());
        reference.setCheck(Boolean.TRUE);
        reference.setRetries(0);
        return ReferenceConfigCache.getCache().get(reference);
    }

    @Override
    public String getGenericServiceMethodName(FunctionDefinition<?> functionDefinition) {
        return functionDefinition.getMethodName();
    }

    @Override
    public String[] getParameterTypes(FunctionDefinition<?> functionDefinition) {
        return functionDefinition.getParameterTypes();
    }
}
