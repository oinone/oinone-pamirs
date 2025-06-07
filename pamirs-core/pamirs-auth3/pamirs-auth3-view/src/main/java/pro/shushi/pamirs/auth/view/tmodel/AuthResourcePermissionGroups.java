package pro.shushi.pamirs.auth.view.tmodel;

import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.view.manager.ResourceNodeEntity;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 资源权限组 - 用于【系统权限】交互使用
 *
 * @author Adamancy Zhang at 19:51 on 2024-01-15
 */
@Base
@Model.model(AuthResourcePermissionGroups.MODEL_MODEL)
@Model(displayName = "资源权限组")
public class AuthResourcePermissionGroups extends TransientModel implements ResourceNodeEntity {

    private static final long serialVersionUID = 8625546008646484729L;

    public static final String MODEL_MODEL = "auth.AuthResourcePermissionGroups";

    // region request parameters

    @Field(displayName = "节点类型")
    private PermissionMateDataEnum nodeType;

    @Field(displayName = "资源ID")
    private Long resourceId;

    @Field(displayName = "资源编码")
    private String resourceCode;

    @Field(displayName = "资源路径")
    private String path;

    // endregion

    // region response parameters

    @Field(displayName = "管理权限组")
    private List<AuthGroup> managementGroups;

    @Field(displayName = "运行时权限组")
    private List<AuthGroup> runtimeGroups;

    // endregion
}
