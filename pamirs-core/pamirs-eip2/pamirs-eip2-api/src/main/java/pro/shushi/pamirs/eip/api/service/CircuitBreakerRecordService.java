package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.model.CircuitBreakerRecord;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

/**
 * @author yeshenyue on 2025/4/17 10:40.
 */
@Fun(CircuitBreakerRecordService.FUN_NAMESPACE)
public interface CircuitBreakerRecordService {

    String FUN_NAMESPACE = "eip.api.CircuitBreakerRecordService";

    @Function
    Pagination<CircuitBreakerRecord> queryPage(Pagination<CircuitBreakerRecord> page, IWrapper<CircuitBreakerRecord> queryWrapper);

    @Function
    void pushRecord(CircuitBreakerRecord breakerRecord);

    @Function
    void saveRecord();

    @Function
    List<CircuitBreakerRecord> findActiveRecords();

    @Function
    Boolean findActiveRecordByInterfaceName(String interfaceName);

    @Function
    void updateEndTime(List<String> interfaceNames);
}
