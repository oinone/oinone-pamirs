package pro.shushi.pamirs.sso.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 返回请求公共类
 *
 * @author wangxian
 */
@Model.model(ApiCommonTransient.MODEL_MODEL)
@Model(displayName = "返回请求公共类")
public class ApiCommonTransient extends TransientModel {

    public final static String MODEL_MODEL = "sso.common.ApiCommon";

    /**
     * 请求返回状态码
     */
    @Field(displayName = "状态码")
    private Integer code;

    /**
     * 返回消息
     */
    @Field(displayName = "返回消息")
    private String msg;

    /**
     * 返回数据对象
     */
    @Field(displayName = "返回数据对象")
    private String data;
}
