package pro.shushi.pamirs.eip.api.strategy.service;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Date;
import java.util.List;

/**
 * @author yeshenyue on 2025/11/6 10:47.
 */
@Fun(EipLogDailyCountService.FUN_NAMESPACE)
public interface EipLogDailyCountService {

    String FUN_NAMESPACE = "eip.EipLogDailyCountService";

    /**
     * 同步昨日日志统计数据
     */
    @Function
    void syncYesterday();

    /**
     * 填充集成接口统计数据
     */
    @Function
    List<EipIntegrationInterface> fillIntegrationLogCountData(List<EipIntegrationInterface> eipIntegrationInterfaceList, Date start, Date end);

    /**
     * 填充开放接口统计数据
     */
    @Function
    List<EipOpenInterface> fillOpenLogCountData(List<EipOpenInterface> eipOpenInterfaceList,Date start, Date end);

    /**
     * 填充昨日集成接口统计数据
     */
    @Function
    List<EipIntegrationInterface> fillIntegrationLogCountData(List<EipIntegrationInterface> eipIntegrationInterfaceList);

    /**
     * 填充昨日开放接口统计数据
     */
    @Function
    List<EipOpenInterface> fillOpenLogCountData(List<EipOpenInterface> eipOpenInterfaceList);
}
