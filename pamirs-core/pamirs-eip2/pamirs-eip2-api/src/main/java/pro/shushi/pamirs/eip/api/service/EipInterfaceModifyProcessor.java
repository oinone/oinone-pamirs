package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;

@FunctionalInterface
public interface EipInterfaceModifyProcessor {

    void accept(InterfaceTypeEnum interfaceType, IEipApi eipApi, Boolean isEnable, Boolean isIgnoreLogConfig);
}
