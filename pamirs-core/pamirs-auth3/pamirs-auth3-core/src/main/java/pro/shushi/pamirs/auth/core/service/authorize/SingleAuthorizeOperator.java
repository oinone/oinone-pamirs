package pro.shushi.pamirs.auth.core.service.authorize;

import pro.shushi.pamirs.auth.api.behavior.*;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.util.List;

/**
 * 单授权操作类
 *
 * @author Adamancy Zhang at 23:12 on 2024-09-11
 */
@Slf4j
public abstract class SingleAuthorizeOperator<
        E extends Enum<E> & PermissionAuthorizedValue,
        PR extends BaseModel & AuthAuthorizationSource & PermissionRelationModel,
        PA extends PermissionAuthorizeModel<E>> {

    protected final String permissionRelationModel;

    protected final StandardModelService<PR> service;

    protected abstract void setAuthorizedValue(PA permission, Long authorizedValue);

    protected abstract PR generatorRolePermission(Long roleId, PA authorizePermission, AuthorizationSourceEnum source);

    public SingleAuthorizeOperator(String permissionRelationModel, StandardModelService<PR> service) {
        this.permissionRelationModel = permissionRelationModel;
        this.service = service;
    }

    protected void assertRoleId(Long roleId) {
        if (roleId == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_ID_NULL_ERROR).errThrow();
        }
    }

    protected void assertPermissionId(Long permissionId) {
        if (permissionId == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_PERMISSION_ID_NULL_ERROR).errThrow();
        }
    }

    protected Long getAuthorizedValue(PA data) {
        Long authorizedValue = data.getAuthorizedValue();
        if (authorizedValue != null) {
            return authorizedValue;
        }
        List<E> authorizedValueEnumList = data.getAuthorizedEnumList();
        if (authorizedValueEnumList != null) {
            authorizedValue = 0L;
            for (E authorizedValueEnum : authorizedValueEnumList) {
                authorizedValue = authorizedValue | authorizedValueEnum.value();
            }
            return authorizedValue;
        }
        throw new IllegalArgumentException("Invalid authorized value.");
    }

    protected void logModifyBuildInWarn(PermissionRelationModel currentPermission) {
        if (log.isWarnEnabled()) {
            log.warn("Modify build-in permission error. currentUserId: {}, roleId = {}, permissionId = {}", PamirsSession.getUserId(), currentPermission.getRoleId(), currentPermission.getPermissionId());
        }
    }

    protected PA update(Long roleId, PA permission, AuthorizationSourceEnum source, AuthorizedValueComputer computer) {
        Long permissionId = permission.getId();
        assertRoleId(roleId);
        assertPermissionId(permissionId);
        Long authorizedValue = getAuthorizedValue(permission);
        PR currentPermission = service.queryOneByWrapper(WrapperHelper.lambda(Pops.<PR>query()
                .from(permissionRelationModel)
                .eq(LambdaUtil.fetchFieldName(AuthRoleResourcePermission::getRoleId), roleId)
                .eq(LambdaUtil.fetchFieldName(AuthRoleResourcePermission::getPermissionId), permissionId)));
        if (currentPermission == null) {
            if (source == null) {
                return null;
            }
            service.create(generatorRolePermission(roleId, permission, source));
        } else {
            if (AuthorizationSourceEnum.BUILD_IN.equals(currentPermission.getSource())) {
                logModifyBuildInWarn(currentPermission);
                return null;
            }
            Long currentAuthorizedValue = currentPermission.getAuthorizedValue();
            authorizedValue = computer.compute(currentAuthorizedValue, authorizedValue);
            if (currentAuthorizedValue.equals(authorizedValue)) {
                return null;
            }
            setAuthorizedValue(permission, authorizedValue);
            service.update(generatorRolePermission(roleId, permission, currentPermission.getSource()));
        }
        return permission;
    }
}
