package pro.shushi.pamirs.user.api.model.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 用户展示名结果。
 * <p>
 * 常见格式由 {@link pro.shushi.pamirs.user.api.spi.UserDisplayNameApi} 决定，例如：
 * <ul>
 *   <li>中文名</li>
 *   <li>中文名-工号/编码</li>
 *   <li>中文名 + subtitle=(部门/职务)</li>
 * </ul>
 */
@Base
@Model.model(UserDisplayName.MODEL_MODEL)
@Model(displayName = "用户展示名")
public class UserDisplayName extends TransientModel {

    private static final long serialVersionUID = 1L;

    public static final String MODEL_MODEL = "user.UserDisplayName";

    @Field.Integer
    @Field(displayName = "用户Id")
    private Long userId;

    @Field.String
    @Field(displayName = "用户编码")
    private String code;

    @Field.String
    @Field(displayName = "展示名称")
    private String displayName;

    @Field.String
    @Field(displayName = "副标题", summary = "如 (财务)、(区域总监)，可空")
    private String subtitle;
}
