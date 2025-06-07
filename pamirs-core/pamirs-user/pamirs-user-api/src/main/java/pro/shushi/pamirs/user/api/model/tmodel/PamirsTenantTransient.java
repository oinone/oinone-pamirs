package pro.shushi.pamirs.user.api.model.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author shier
 * date  2022/8/3 4:10 下午
 */
@Model.model(PamirsTenantTransient.MODEL_MODEL)
@Model(displayName = "租户", summary = "用于单个用户属于多个租户时候的交互选择")
public class PamirsTenantTransient extends TransientModel {

    private static final long serialVersionUID = -3420628738487428260L;

    public static final String MODEL_MODEL = "user.PamirsTenantTransient";

    @Field.String
    @Field(displayName = "显示名称")
    private String displayName;

    @Field.String
    @Field(displayName = "租户编码", summary = "用于数据的传输")
    private String tenant;

}
