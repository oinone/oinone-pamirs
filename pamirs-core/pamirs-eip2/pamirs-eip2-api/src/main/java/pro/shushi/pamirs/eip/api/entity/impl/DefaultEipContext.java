package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.entity.AbstractEipContext;

import java.util.Map;

/**
 * 默认使用SuperMap作为上下文承载对象
 */
public class DefaultEipContext extends AbstractEipContext<SuperMap> implements IEipContext<SuperMap> {

    public DefaultEipContext(IEipApi eipApi, SuperMap executorContext, SuperMap interfaceContext) {
        super(eipApi, executorContext == null ? new SuperMap() : executorContext, interfaceContext == null ? new SuperMap() : interfaceContext);
    }

    @Override
    public Object getExecutorContextValue(String key) {
        return getExecutorContext().getIteration(key);
    }

    @Override
    public void putExecutorContextValue(String key, Object value) {
        getExecutorContext().putIteration(key, value);
    }

    @Override
    public void putAllExecutorContextValue(Map<? extends String, ?> map) {
        getExecutorContext().putAllIteration(map);
    }

    @Override
    public Object getInterfaceContextValue(String key) {
        return getInterfaceContext().getIteration(key);
    }

    @Override
    public void putInterfaceContextValue(String key, Object value) {
        getInterfaceContext().putIteration(key, value);
    }

    @Override
    public void putAllInterfaceContextValue(Map<? extends String, ?> map) {
        getInterfaceContext().putAllIteration(map);
    }
}
