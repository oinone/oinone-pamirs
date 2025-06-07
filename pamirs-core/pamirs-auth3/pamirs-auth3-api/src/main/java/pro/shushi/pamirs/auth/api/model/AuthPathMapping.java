package pro.shushi.pamirs.auth.api.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.behavior.AuthAuthorizationSource;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.List;
import java.util.Optional;

/**
 * 权限路径映射
 *
 * @author Adamancy Zhang at 14:45 on 2024-03-25
 */
@Base
@Model.model(AuthPathMapping.MODEL_MODEL)
@Model.Advanced(unique = {"code"/*, "resModel,resId"*/})
@Model(displayName = "权限路径映射")
public class AuthPathMapping extends IdModel implements AuthAuthorizationSource {

    private static final long serialVersionUID = 3298125061832871252L;

    public static final String MODEL_MODEL = "auth.AuthPathMapping";

    @Field.String(size = 64)
    @Field(displayName = "路径编码")
    private String code;

    @Field.String
    @Field(displayName = "权限节点ID")
    private String permissionNodeId;

    @Field.String
    @Field(displayName = "权限节点类型")
    private ResourcePermissionSubtypeEnum permissionNodeType;

    @Field.Text
    @Field(displayName = "源路径")
    private String originPath;

    @Field.String
    @Field.Advanced(columnDefinition = "text")
    @Field(displayName = "目标路径", multi = true)
    private List<String> targetPathList;

    @Field.Enum
    @Field(displayName = "权限项来源", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.String
    @Field(displayName = "权限节点JSON", store = NullableBoolEnum.FALSE)
    private String nodeJson;

    private transient PermissionNode node;

    public static String generatorCode(String path) {
        return EncryptHelper.shortCode(path);
    }

    public PermissionNode parseNode() {
        PermissionNode node = getNode();
        if (node != null) {
            return node;
        }
        node = Optional.ofNullable(getNodeJson())
                .filter(StringUtils::isNotBlank)
                .map(json -> JsonUtils.parseObject(json, PermissionNode.class))
                .orElse(null);
        return node;
    }
}
