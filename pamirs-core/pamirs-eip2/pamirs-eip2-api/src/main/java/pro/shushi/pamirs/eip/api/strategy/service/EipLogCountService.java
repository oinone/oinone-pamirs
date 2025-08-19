package pro.shushi.pamirs.eip.api.strategy.service;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.statistics.EipLogCount;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * EIP日志统计服务
 *
 * @author yeshenyue on 2025/4/10 10:22.
 */
@Fun(EipLogCountService.FUN_NAMESPACE)
public interface EipLogCountService {

    String FUN_NAMESPACE = "eip.api.EipLogCountService";

    /**
     * 填充集成接口日志统计
     */
    @Function
    void fillEipIntegrationInterfaceLogCount(List<EipIntegrationInterface> eipIntegrationInterfaceList);

    /**
     * 填充开放接口日志统计
     */
    @Function
    void fillEipOpenInterfaceLogCount(List<EipOpenInterface> eipOpenInterfaceList);

    /**
     * 根据接口名称查询日志统计
     */
    @Function
    List<EipLogCount> queryListByInterfaceName(InterfaceTypeEnum interfaceType, List<String> interfaceNameList);

    /**
     * 全量同步接口日志统计
     */
    @Function
    void syncEipLogCount();
}
