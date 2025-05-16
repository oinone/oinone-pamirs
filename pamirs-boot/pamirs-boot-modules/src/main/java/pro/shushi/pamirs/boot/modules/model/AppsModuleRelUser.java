package pro.shushi.pamirs.boot.modules.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * @author shier
 * date  2021/6/21 2:38 下午
 */
@Base
@Model.model(AppsModuleRelUser.MODEL_MODEL)
@Model(displayName = "Apps应用关联用户")
@Model.Advanced(unique = "module,userId")
public class AppsModuleRelUser extends IdModel {

    public final static String MODEL_MODEL = "apps.AppsModuleRelUser";

    @Base
    @Field(displayName = "模块编码", required = true)
    private String module;

    @Field.Integer
    @Field(displayName = "用户id", required = true)
    private Long userId;

    @Field.Boolean
    @Field(defaultValue = "false")
    private Boolean like;

    @Field.Integer
    @Field(displayName = "排序数",defaultValue = "0")
    private Long orderNumber;
}
