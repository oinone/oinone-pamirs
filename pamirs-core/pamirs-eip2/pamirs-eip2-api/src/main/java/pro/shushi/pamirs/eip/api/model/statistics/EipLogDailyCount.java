package pro.shushi.pamirs.eip.api.model.statistics;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.DateTypeEnum;

import java.util.Date;

/**
 * @author yeshenyue on 2025/11/6 10:42.
 */
@Model(displayName = "EIP日志按天调用统计")
@Model.model(EipLogDailyCount.MODEL_MODEL)
@Model.Advanced(unique = {"interfaceName,interfaceType,countDate"},ordering = "interface_name")

public class EipLogDailyCount extends IdModel {

    public static final String MODEL_MODEL = "pamirs.eip.EipLogDailyCount";

    private static final long serialVersionUID = 324205313492284139L;

    @Field.String
    @Field(displayName = "接口技术名称")
    private String interfaceName;

    @Field.Enum
    @Field(displayName = "接口类型", required = true)
    private InterfaceTypeEnum interfaceType;

    @Field.Date(type = DateTypeEnum.DATE, format = DateFormatEnum.DATE)
    @Field(displayName = "统计日期")
    private Date countDate;

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
        return getCountValue(EipLogDailyCount::getSuccessCallCount);
    }

    public Long getFailCallCount() {
        return getCountValue(EipLogDailyCount::getFailCallCount);
    }

    public Long getUltraFastCall() {
        return getCountValue(EipLogDailyCount::getUltraFastCall);
    }

    public Long getVeryFastCall() {
        return getCountValue(EipLogDailyCount::getVeryFastCall);
    }

    public Long getFastCall() {
        return getCountValue(EipLogDailyCount::getFastCall);
    }

    public Long getModerateCall() {
        return getCountValue(EipLogDailyCount::getModerateCall);
    }

    public Long getSlowCall() {
        return getCountValue(EipLogDailyCount::getSlowCall);
    }

    public Long getVerySlowCall() {
        return getCountValue(EipLogDailyCount::getVerySlowCall);
    }

    public Long getTimeoutCall() {
        return getCountValue(EipLogDailyCount::getTimeoutCall);
    }

    public Long getSlowestCall() {
        return getCountValue(EipLogDailyCount::getSlowestCall);
    }

    private <T, R> Long getCountValue(Getter<T, R> methodReference) {
        String field = LambdaUtil.fetchFieldName(methodReference);
        Object value = get_d().get(field);
        return value != null ? (Long) value : 0L;
    }
}
