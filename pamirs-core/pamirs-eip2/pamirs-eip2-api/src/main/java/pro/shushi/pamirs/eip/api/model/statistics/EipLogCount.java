package pro.shushi.pamirs.eip.api.model.statistics;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

/**
 * @author yeshenyue on 2025/4/10 09:23.
 */
@Model.Advanced(unique = {"interfaceName,interfaceType"})
@Model(displayName = "集成/开放接口调用统计")
@Model.model(EipLogCount.MODEL_MODEL)
public class EipLogCount extends IdModel {

    public static final String MODEL_MODEL = "eip.api.EipLogCount";
    private static final long serialVersionUID = -5238133156930582749L;

    @Field.String
    @Field(displayName = "接口技术名称")
    private String interfaceName;

    @Field.Enum
    @Field(displayName = "接口类型", required = true)
    private InterfaceTypeEnum interfaceType;

    @Field.Integer
    @Field(displayName = "成功调用次数")
    private Long successCallCount;

    @Field.Integer
    @Field(displayName = "失败调用次数")
    private Long failCallCount;

    @Field.Integer
    @Field(displayName = "小于100ms调用次数")
    private Long ultraFastCall;

    @Field.Integer
    @Field(displayName = "100-300ms调用数量")
    private Long veryFastCall;

    @Field.Integer
    @Field(displayName = "300-500ms调用数量")
    private Long fastCall;

    @Field.Integer
    @Field(displayName = "500-1000ms调用数量")
    private Long moderateCall;

    @Field.Integer
    @Field(displayName = "1s-3s调用数量")
    private Long slowCall;

    @Field.Integer
    @Field(displayName = "3s-8s调用数量")
    private Long verySlowCall;

    @Field.Integer
    @Field(displayName = "8s-30s调用数量")
    private Long slowestCall;

    @Field.Integer
    @Field(displayName = "大于30s调用数量")
    private Long timeoutCall;

    public Long getSuccessCallCount() {
        return getCountValue(EipLogCount::getSuccessCallCount);
    }

    public Long getFailCallCount() {
        return getCountValue(EipLogCount::getFailCallCount);
    }

    public Long getUltraFastCall() {
        return getCountValue(EipLogCount::getUltraFastCall);
    }

    public Long getVeryFastCall() {
        return getCountValue(EipLogCount::getVeryFastCall);
    }

    public Long getFastCall() {
        return getCountValue(EipLogCount::getFastCall);
    }

    public Long getModerateCall() {
        return getCountValue(EipLogCount::getModerateCall);
    }

    public Long getSlowCall() {
        return getCountValue(EipLogCount::getSlowCall);
    }

    public Long getVerySlowCall() {
        return getCountValue(EipLogCount::getVerySlowCall);
    }

    public Long getTimeoutCall() {
        return getCountValue(EipLogCount::getTimeoutCall);
    }

    public Long getSlowestCall() {
        return getCountValue(EipLogCount::getSlowestCall);
    }

    private <T, R> Long getCountValue(Getter<T, R> methodReference) {
        String field = LambdaUtil.fetchFieldName(methodReference);
        Object value = get_d().get(field);
        return value != null ? (Long) value : 0L;
    }
}
