package pro.shushi.pamirs.eip.api.service.distribution;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.meta.api.dto.common.Result;

import java.util.List;

/**
 * EIP 分布式支持
 *
 * @author Adamancy Zhang at 13:04 on 2020-09-27
 */
public interface EipDistributionSupport extends EipDistributionService {

    String NODE_PATH_PREFIX = "/eip/api";

    byte[] ENABLED = new byte[]{1};

    byte[] DISABLED = new byte[]{0};

    /**
     * 注册监听
     */
    void registerListener(List<String> tenantRootPathList);

    /**
     * 刷新路由定义
     */
    Result<String> refreshRouteDefinition(EipRouteDefinition eipInterface);

    /**
     * 刷新集成接口
     */
    Result<String> refreshInterface(EipIntegrationInterface eipInterface);

    /**
     * 刷新开放接口
     */
    Result<String> refreshOpenInterface(EipOpenInterface eipInterface);
}
