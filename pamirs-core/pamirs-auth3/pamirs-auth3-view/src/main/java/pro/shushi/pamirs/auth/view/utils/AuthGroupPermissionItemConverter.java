package pro.shushi.pamirs.auth.view.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.auth.view.model.AuthRowPermissionItem;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限组的权限项转换帮助类
 *
 * @author Adamancy Zhang at 14:01 on 2024-02-02
 */
public class AuthGroupPermissionItemConverter {

    private AuthGroupPermissionItemConverter() {
        // reject create object
    }

    public static List<AuthRowPermissionItem> convertRowPermissionItems(List<AuthGroupRowPermission> groupRowPermissions) {
        if (CollectionUtils.isEmpty(groupRowPermissions)) {
            return new ArrayList<>();
        }
        return groupRowPermissions.stream().map(AuthGroupPermissionItemConverter::convertRowPermissionItem).collect(Collectors.toList());
    }

    public static AuthRowPermissionItem convertRowPermissionItem(AuthGroupRowPermission rowPermission) {
        AuthRowPermissionItem permission = new AuthRowPermissionItem();
        AuthRowPermission.transfer(rowPermission.getPermission(), permission);
        Long authorizedValue = rowPermission.getAuthorizedValue();
        String model = permission.getModel();
        if (StringUtils.isNotBlank(model)) {
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
            if (modelConfig == null) {
                permission.setModelDefinition(null);
            } else {
                ModelDefinition modelDefinition = new ModelDefinition();
                modelDefinition.setModel(modelConfig.getModel());
                modelDefinition.setDisplayName(modelConfig.getDisplayName());
                permission.setModelDefinition(modelDefinition);
            }
        }
        permission.setDomainExp(permission.getFilter());
        permission.setPermRead(RowAuthorizedValueEnum.readable(authorizedValue));
        permission.setPermWrite(RowAuthorizedValueEnum.writable(authorizedValue));
        permission.setPermDelete(RowAuthorizedValueEnum.deletable(authorizedValue));
        return permission;
    }
}
