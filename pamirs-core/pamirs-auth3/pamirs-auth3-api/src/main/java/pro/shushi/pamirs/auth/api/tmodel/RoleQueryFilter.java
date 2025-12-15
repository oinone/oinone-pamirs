package pro.shushi.pamirs.auth.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 角色查询条件模型
 *
 * @author Adamancy Zhang at 14:15 on 2025-12-05
 */
@Base
@Model(displayName = "角色查询条件模型")
@Model.model(RoleQueryFilter.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class RoleQueryFilter extends TransientModel {

    private static final long serialVersionUID = -3047244820168259761L;

    public static final String MODEL_MODEL = "auth.RoleQueryFilter";

    @Field(displayName = "RSQL过滤条件")
    private String rsql;

    @Field(displayName = "角色编码")
    private List<String> roleCodes;

    @Field(displayName = "当前用户所绑定角色")
    private Boolean userRole;

}
