package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipParamMapping;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * @author yeshenyue on 2024/9/6 11:42.
 */
@Fun(EipParamMappingService.FUN_NAMESPACE)
public interface EipParamMappingService {

    String FUN_NAMESPACE = "pamirs.eip.EipParamMappingService";

    @Function
    EipParamMapping createOrUpdate(EipParamMapping data);

    /**
     * 创建或更新，并处理前端表达式
     */
    @Function
    EipParamMapping createOrUpdateWithExp(EipParamMapping data);
}
