package pro.shushi.pamirs.auth.api.model.permission;

import pro.shushi.pamirs.auth.api.behavior.AuthPermission;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

/**
 * 行权限项
 *
 * @author Adamancy Zhang at 16:10 on 2024-01-04
 */
@Base
@Model.model(AuthRowPermission.MODEL_MODEL)
@Model.Advanced(unique = {"code"})
@Model(displayName = "行权限项", labelFields = {"displayName"})
public class AuthRowPermission extends IdModel implements AuthPermission {

    private static final long serialVersionUID = -7181215901532856416L;

    public static final String MODEL_MODEL = "auth.AuthRowPermission";

    @Field.String(size = 64)
    @Field(displayName = "资源编码")
    private String code;

    @Field.String
    @Field(displayName = "名称")
    private String displayName;

    @Field.String
    @Field(displayName = "描述")
    private String description;

    @Field.Text
    @Field(displayName = "资源路径", required = true)
    private String path;

    @Field.String
    @Field(displayName = "模型编码", required = true)
    private String model;

    @Field.Text
    @Field(displayName = "过滤条件", required = true)
    private String filter;

    /**
     * 表达式改为前端解析
     */
    @Deprecated
    @Field.String(size = 4096)
    @Field(displayName = "表达式", summary = "过滤的数据表达式的展示字段")
    private String domainExpDisplayName;

    /**
     * 表达式改为前端解析
     */
    @Deprecated
    @Field.Text
    @Field(displayName = "表达式解析", summary = "用于前端交互,表达式的json解析")
    private String domainExpJson;

    @Field.Enum
    @Field(displayName = "权限项来源", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.Boolean
    @Field(displayName = "是否激活", defaultValue = "true")
    private Boolean active;

    public static String generatorCode() {
        return UUIDUtil.getUUIDNumberString();
    }

    public static String generatorCode(String name) {
        return EncryptHelper.shortCode(name);
    }

    public static <T extends AuthRowPermission> T transfer(AuthRowPermission origin, T target) {
        target.setId(origin.getId());
        target.setCode(origin.getCode());
        target.setDisplayName(origin.getDisplayName());
        target.setDescription(origin.getDescription());
        target.setPath(origin.getPath());
        target.setModel(origin.getModel());
        target.setFilter(origin.getFilter());
        target.setDomainExpDisplayName(origin.getDomainExpDisplayName());
        target.setDomainExpJson(origin.getDomainExpJson());
        target.setSource(origin.getSource());
        target.setActive(origin.getActive());
        return target;
    }
}
