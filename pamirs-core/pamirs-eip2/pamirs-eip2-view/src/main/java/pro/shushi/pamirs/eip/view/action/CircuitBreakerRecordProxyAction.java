package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.statistics.CircuitBreakerRecord;
import pro.shushi.pamirs.eip.api.pmodel.CircuitBreakerRecordProxy;
import pro.shushi.pamirs.eip.api.strategy.service.EipCircuitBreakerRecordService;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * @author yeshenyue on 2025/4/17 10:39.
 */
@Component
@Model.model(CircuitBreakerRecordProxy.MODEL_MODEL)
public class CircuitBreakerRecordProxyAction {

    @Autowired
    private EipCircuitBreakerRecordService eipCircuitBreakerRecordService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<CircuitBreakerRecordProxy> queryPage(
            Pagination<CircuitBreakerRecordProxy> page,
            IWrapper<CircuitBreakerRecordProxy> queryWrapper) {

        Pagination<CircuitBreakerRecord> recordPage = ArgUtils.convert(
                CircuitBreakerRecordProxy.MODEL_MODEL,
                CircuitBreakerRecord.MODEL_MODEL,
                page
        );
        IWrapper<CircuitBreakerRecord> recordWrapper = ArgUtils.convert(
                CircuitBreakerRecordProxy.MODEL_MODEL,
                CircuitBreakerRecord.MODEL_MODEL,
                queryWrapper
        );

        Pagination<CircuitBreakerRecord> recordResult = eipCircuitBreakerRecordService.queryPage(recordPage, recordWrapper);
        Pagination<CircuitBreakerRecordProxy> result = ArgUtils.convert(
                CircuitBreakerRecordProxy.MODEL_MODEL,
                CircuitBreakerRecord.MODEL_MODEL,
                recordResult
        );
        return result;
    }
}
