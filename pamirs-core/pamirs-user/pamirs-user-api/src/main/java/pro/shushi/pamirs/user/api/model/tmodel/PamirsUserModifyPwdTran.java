package pro.shushi.pamirs.user.api.model.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 *
 * 用户修改密码临时模型
 * @author shier
 * date  2022/7/4 下午8:07
 */
@Model
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model.model(PamirsUserModifyPwdTran.MODEL_MODEL)
public class PamirsUserModifyPwdTran extends TransientModel {

    public static final String MODEL_MODEL = "user.PamirsUserModifyPwdTran";

    @Field.Integer
    @Field(displayName = "用户Id")
    private Long id;

    @Field.String
    @Field(displayName = "账号")
    private String login;

    @Field.String
    @Field(displayName = "账号确定")
    private String confirmLogin;

    @Field.String
    @Field(displayName = "重置后新密码")
    private String resetPassword;

}
