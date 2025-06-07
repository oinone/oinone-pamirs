package pro.shushi.pamirs.eip.api.pmodel;

import pro.shushi.pamirs.eip.api.model.CircuitBreakerRecord;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author yeshenyue on 2025/4/17 10:19.
 */
@Model(displayName = "接口熔断记录代理")
@Model.model(CircuitBreakerRecordProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class CircuitBreakerRecordProxy extends CircuitBreakerRecord {

    public static final String MODEL_MODEL = "eip.CircuitBreakerRecordProxy";
    private static final long serialVersionUID = 6907009222485544319L;

}
