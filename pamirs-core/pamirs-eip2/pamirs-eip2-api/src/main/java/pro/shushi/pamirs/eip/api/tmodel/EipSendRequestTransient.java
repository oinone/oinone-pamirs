package pro.shushi.pamirs.eip.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * @author yeshenyue on 2024/8/21 13:37.
 */
@Model(displayName = "集成接口请求发送")
@Model.model(EipSendRequestTransient.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class EipSendRequestTransient extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.eip.tmodel.EipSendRequestTransient";
    private static final long serialVersionUID = 4420650066441308674L;

    @Field.String
    @Field(displayName = "接口技术名称", required = true)
    private String interfaceName;

    @Field.String
    @Field(displayName = "模型编码", required = true)
    private String model;

    @Field.String
    @Field(displayName = "视图名称", required = true)
    private String viewName;

    @Field.String
    @Field(displayName = "服务器动作名称", required = true)
    private String actionName;

    @Field.Text
    @Field(displayName = "请求参数", store = NullableBoolEnum.FALSE)
    private String requestData;

    @Field(displayName = "响应参数", store = NullableBoolEnum.FALSE)
    private Object responseData;
}
