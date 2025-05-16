package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.resource.api.enmu.CurrencyRateServiceEnum;

import java.util.Date;

@Model.model(ResourceAutoCurrencyRateConfig.MODEL_MODEL)
@Model(displayName = "自动汇率配置")
public class ResourceAutoCurrencyRateConfig extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceAutoCurrencyRateConfig";

    @Field.Boolean
    @Field(displayName = "启用自动汇率", summary = "是否启用")
    private Boolean enable;

    @Field.Enum
    @Field(displayName = "汇率服务")
    private CurrencyRateServiceEnum currencyService;

    @Field.Integer
    @Field(displayName = "更新间隔")
    private Integer intervalTime;

    @Field.Date
    @Field(displayName = "下次执行时间")
    private Date nextExecuteTime;

    @Field.Date
    @Field(displayName = "上次执行时间")
    private Date lastExecuteTime;


}
