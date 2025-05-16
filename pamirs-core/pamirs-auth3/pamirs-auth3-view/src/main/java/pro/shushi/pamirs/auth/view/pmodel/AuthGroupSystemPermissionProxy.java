package pro.shushi.pamirs.auth.view.pmodel;

import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.view.manager.ResourceNodeEntity;
import pro.shushi.pamirs.auth.view.model.AuthActionPermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthFieldPermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthRowPermissionItem;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 权限组 - 系统权限权限组
 *
 * @author Adamancy Zhang at 16:49 on 2024-01-16
 */
@Model.model(AuthGroupSystemPermissionProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY, unInheritedFunctions = {FunctionConstants.deleteWithFieldBatch})
@Model(displayName = "系统权限权限组")
public class AuthGroupSystemPermissionProxy extends AuthGroup implements ResourceNodeEntity {

    private static final long serialVersionUID = -6864385157747997577L;

    public static final String MODEL_MODEL = "auth.AuthGroupSystemPermissionProxy";

    @Field(displayName = "模型")
    private String model;

    @Field(displayName = "节点类型")
    private PermissionMateDataEnum nodeType;

    @Field(displayName = "资源ID")
    private Long resourceId;

    @Field(displayName = "资源编码")
    private String resourceCode;

    @Field(displayName = "资源显示名称")
    private String resourceDisplayName;

    @Field(displayName = "资源路径")
    private String path;

    @Field(displayName = "动作权限")
    private List<AuthActionPermissionItem> actionPermissions;

    @Field(displayName = "字段权限")
    private List<AuthFieldPermissionItem> fieldPermissions;

    @Field(displayName = "行权限")
    private AuthRowPermissionItem rowPermission;

    @Field(displayName = "版本信息")
    @Field.Boolean
    private Boolean enterpriseEdition;
}
