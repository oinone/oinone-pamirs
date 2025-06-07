package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.EipOpenRateLimitPolicy;

import pro.shushi.pamirs.eip.api.pmodel.EipApplicationProxy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * @author yeshenyue on 2025/4/21 14:35.
 */
@Fun(EipOpenRateLimitPolicyService.FUN_NAMESPACE)
public interface EipOpenRateLimitPolicyService {

    String FUN_NAMESPACE = "eip.EipOpenRateLimitPolicyService";

    /**
     * 初始化
     */
    @Function
    void init();

    /**
     * 批量创建or修改流控策略
     */
    @Function
    List<EipOpenRateLimitPolicy> batchModifyFlowControlPolicies(List<EipOpenRateLimitPolicy> dataList);

    /**
     * 根据开放应用查询流控策略，不存在的流控策略会填充null
     */
    @Function
    List<EipOpenRateLimitPolicy> queryListByApplicationCode(EipApplicationProxy applicationProxy);

    /**
     * 删除应用下所有流控策略
     */
    @Function
    void removeAll(EipApplication application);

    /**
     * 删除流控配置
     */
    @Function
    void deleteByInterfaceName(EipApplication application, List<String> interfaceNames);

    /**
     * 更新本地流控配置
     */
    @Function
    void refreshLocal(String appKey, String interfaceName);
}
