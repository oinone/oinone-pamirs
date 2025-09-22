package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipInterfaceTestTransient;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * @author Adamancy Zhang at 18:06 on 2025-08-12
 */
@Fun(EipInterfaceTestService.FUN_NAMESPACE)
public interface EipInterfaceTestService {

    String FUN_NAMESPACE = "pamirs.eip.EipInterfaceTestService";

    @Function
    EipInterfaceTestTransient construct(EipInterfaceTestTransient data, EipIntegrationInterface integrationInterface);

    @Function
    EipInterfaceTestTransient mockParamConverter(EipInterfaceTestTransient data);

    @Function
    EipInterfaceTestTransient test(EipInterfaceTestTransient data);

}
