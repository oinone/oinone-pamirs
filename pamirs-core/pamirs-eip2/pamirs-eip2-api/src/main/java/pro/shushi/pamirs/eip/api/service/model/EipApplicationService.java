package pro.shushi.pamirs.eip.api.service.model;


import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.meta.annotation.Fun;

import java.util.List;

@Fun(EipApplicationService.FUN_NAMESPACE)
public interface EipApplicationService {
    String FUN_NAMESPACE = "pamirs.eip.EipApplicationService";

    EipApplication create(EipApplication data);

    EipApplication update(EipApplication data);

    Boolean enable(EipApplication data);

    Boolean disable(EipApplication data);

    EipApplication queryByAppKey(String appKey);

    List<EipApplication> queryByCodes(List<String> codes);
}

