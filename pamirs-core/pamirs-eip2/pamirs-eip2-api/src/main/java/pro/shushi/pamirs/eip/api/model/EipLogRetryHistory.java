package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;

import java.util.Date;

/**
 * @author yeshenyue on 2026/4/3 16:01.
 */
@Base
@Model(displayName = "接口日志重试调用纪录")
@Model.Advanced(index = {"logId"})
@Model.model(EipLogRetryHistory.MODEL_MODEL)
public class EipLogRetryHistory extends IdModel {

    public static final String MODEL_MODEL = "eip.EipLogRetryHistory";

    @Base
    @Field.Integer
    @Field(displayName = "日志ID")
    private Long logId;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "请求头数据")
    private String requestHeaderData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "原始请求数据")
    private String requestOriginData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "真实请求数据")
    private String requestTargetData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "响应头数据")
    private String responseHeaderData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "响应数据")
    private String responseData;

    @Base
    @Field.Boolean
    @Field(displayName = "是否成功调用")
    private Boolean isSuccess;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "异常信息")
    private String errorMsg;

    @Base
    @Field.Date(fraction = 3, format = DateFormatEnum.DATETIME_SSS)
    @Field(displayName = "调用时间")
    private Date invokeDate;

    @Base
    @Field.Date(fraction = 3, format = DateFormatEnum.DATETIME_SSS)
    @Field(displayName = "调用完成时间")
    private Date invokeEndDate;

    @Base
    @Field.Integer
    @Field(displayName = "调用时长(ms)")
    private Long invokeMillisecond;
}
