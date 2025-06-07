package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipParamMapping;
import pro.shushi.pamirs.eip.api.tmodel.EipSendRequestTransient;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * @author yeshenyue on 2024/9/6 15:59.
 */
@Fun(EipSendRequestTransientService.FUN_NAMESPACE)
public interface EipSendRequestTransientService {

    String FUN_NAMESPACE = "pamirs.eip.EipSendRequestTransientService";

    /**
     * 发送Eip请求
     *
     * @param eipInterface 集成接口
     * @param paramMapping 映射参数
     * @param modelData    模型数据
     * @param model        模型编码
     */
    @Function
    EipSendRequestTransient sendEipRequest(EipIntegrationInterface eipInterface, EipParamMapping paramMapping, String modelData, String model);

}
