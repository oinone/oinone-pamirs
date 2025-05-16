package pro.shushi.pamirs.auth.api.model.permission;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.behavior.AuthPermission;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 字段权限项
 *
 * @author Adamancy Zhang at 16:05 on 2024-01-04
 */
@Base
@Model.model(AuthFieldPermission.MODEL_MODEL)
@Model.Advanced(unique = {"code,model,field"})
@Model(displayName = "字段权限项")
public class AuthFieldPermission extends IdModel implements AuthPermission {

    private static final long serialVersionUID = 6935068118255367468L;

    public static final String MODEL_MODEL = "auth.AuthFieldPermission";

    @Field.String(size = 64)
    @Field(displayName = "资源编码")
    private String code;

    @Field.Text
    @Field(displayName = "资源路径", required = true)
    private String path;

    @Field.String
    @Field(displayName = "模型编码", required = true)
    private String model;

    @Field.String
    @Field(displayName = "字段编码", required = true)
    private String field;

    @Field.Enum
    @Field(displayName = "权限项来源", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.Boolean
    @Field(displayName = "是否激活", defaultValue = "true")
    private Boolean active;

    @Override
    public String refreshCode() {
        String code = generatorCode(getModel(), getField(), getPath());
        setCode(code);
        return code;
    }

    @Override
    public String sign() {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE, getModel(), getField(), getCode());
    }

    public static String generatorCode(String model, String field) {
        return generatorCode(model, field, null);
    }

    public static String generatorCode(String model, String field, String path) {
        StringBuilder builder = new StringBuilder(model)
                .append(CharacterConstants.SEPARATOR_OCTOTHORPE)
                .append(field);
        if (StringUtils.isNotBlank(path)) {
            builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE).append(path);
        }
        return EncryptHelper.shortCode(builder.toString());
    }

    public static <T extends AuthFieldPermission> T transfer(AuthFieldPermission origin, T target) {
        target.setId(origin.getId());
        target.setCode(origin.getCode());
        target.setPath(origin.getPath());
        target.setModel(origin.getModel());
        target.setField(origin.getField());
        target.setSource(origin.getSource());
        target.setActive(origin.getActive());
        return target;
    }
}
