package pro.shushi.pamirs.auth.api.model.permission;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.behavior.AuthPermission;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 资源权限项
 *
 * @author Adamancy Zhang at 14:43 on 2024-01-04
 */
@Base
@Model.model(AuthResourcePermission.MODEL_MODEL)
@Model.Advanced(unique = {"code,type,subtype"}, index = {"model,name"})
@Model(displayName = "资源权限项")
public class AuthResourcePermission extends IdModel implements AuthPermission {

    private static final long serialVersionUID = 6000794472699138237L;

    public static final String MODEL_MODEL = "auth.AuthResourcePermission";

    @Field.String(size = 64)
    @Field(displayName = "资源编码")
    private String code;

    /**
     * 资源显示名称仅为自定义资源权限项时有值，系统资源权限项根据系统资源动态获取
     */
    @Field.String
    @Field(displayName = "资源显示名称")
    private String displayName;

    @Field.Text
    @Field(displayName = "资源路径", required = true)
    private String path;

    @Field.String
    @Field(displayName = "资源模块")
    private String module;

    @Field.String
    @Field(displayName = "资源模型")
    private String model;

    @Field.String
    @Field(displayName = "资源名称")
    private String name;

    @Field.Enum
    @Field(displayName = "资源类型", required = true)
    private ResourcePermissionTypeEnum type;

    @Field.Enum
    @Field(displayName = "资源子类型", required = true)
    private ResourcePermissionSubtypeEnum subtype;

    @Field.Enum
    @Field(displayName = "权限项来源", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.Boolean
    @Field(displayName = "是否激活", defaultValue = "true")
    private Boolean active;

    @Override
    public String refreshCode() {
        String code = generatorCode(getModule(), getModel(), getName(), getPath());
        setCode(code);
        return code;
    }

    @Override
    public String sign() {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE, getType().name(), getSubtype().name(), getCode());
    }

    public static String generatorCode(String model, String name) {
        return generatorCode(null, model, name, null);
    }

    public static String generatorCode(String model, String name, String path) {
        return generatorCode(null, model, name, path);
    }

    public static String generatorCode(String module, String model, String name, String path) {
        StringBuilder builder;
        if (StringUtils.isBlank(module)) {
            builder = new StringBuilder();
        } else {
            builder = new StringBuilder(module).append(CharacterConstants.SEPARATOR_OCTOTHORPE);
        }
        builder.append(model).append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(name);
        if (StringUtils.isNotBlank(path)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(path);
        }
        return EncryptHelper.shortCode(builder.toString());
    }

    public static <T extends AuthResourcePermission> T transfer(AuthResourcePermission origin, T target) {
        target.setId(origin.getId());
        target.setCode(origin.getCode());
        target.setPath(origin.getPath());
        target.setModule(origin.getModule());
        target.setModel(origin.getModel());
        target.setName(origin.getName());
        target.setType(origin.getType());
        target.setSubtype(origin.getSubtype());
        target.setSource(origin.getSource());
        target.setActive(origin.getActive());
        return target;
    }
}
