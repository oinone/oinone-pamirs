package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.Date;

@Model.model(EipIncUpdateCursor.MODEL_MODEL)
@Model.Advanced(unique = "interfaceName")
@Model(displayName = "增量游标", labelFields = "name")
public class EipIncUpdateCursor extends IdModel {

    public static final String MODEL_MODEL = "pamirs.eip.EipIncUpdateCursor";

    @Field.String
    @Field(displayName = "接口调用方别名")
    private String consumerAlias;

    @Field.String
    @Field(displayName = "EIP方法名")
    private String interfaceName;

    @Field.Text
    @Field(displayName = "请求参数")
    private String params;

    @Field.Date
    @Field(displayName = "开始时间")
    private Date startTime;

    @Field.Date
    @Field(displayName = "结束时间")
    private Date endTime;

    @Field.Date
    @Field(displayName = "最后请求时间")
    private Date lastUpdateTime;
}
