package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.entity.EipResult;

public interface IEipExceptionHandler {

    boolean match(EipResult<?> eipResult, Throwable e);

    EipResult<?> handler(EipResult<?> eipResult, Throwable e);
}
