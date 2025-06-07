package pro.shushi.pamirs.auth.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.util.List;
import java.util.Set;

/**
 * 角色服务实现
 *
 * @author Adamancy Zhang at 16:16 on 2024-01-06
 */
@Service
@Fun(AuthRoleService.FUN_NAMESPACE)
public class AuthRoleServiceImpl extends AbstractStandardModelService<AuthRole> implements AuthRoleService {

    @Function
    @Override
    public AuthRole create(AuthRole data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthRole> createBatch(List<AuthRole> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthRole update(AuthRole data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthRole data, LambdaUpdateWrapper<AuthRole> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthRole> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthRole createOrUpdate(AuthRole data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthRole> delete(List<AuthRole> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthRole deleteOne(AuthRole data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthRole> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthRole> queryPage(Pagination<AuthRole> page, LambdaQueryWrapper<AuthRole> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthRole queryOne(AuthRole query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthRole queryOneByWrapper(LambdaQueryWrapper<AuthRole> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthRole> queryListByWrapper(LambdaQueryWrapper<AuthRole> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthRole> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Function
    @Override
    public List<AuthRole> fetchRoles(Set<Long> ids) {
        return DataShardingHelper.build().collectionSharding(ids, (sublist) -> queryListByWrapper(Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .in(AuthRole::getId, sublist)));
    }

    @Function
    @Override
    public List<AuthRole> fetchActiveRoles(Set<Long> ids) {
        return DataShardingHelper.build().collectionSharding(ids, (sublist) -> queryListByWrapper(Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .eq(AuthRole::getActive, Boolean.TRUE)
                .in(AuthRole::getId, sublist)));
    }

    @Function
    @Override
    public Boolean active(Long id) {
        Integer effectRow = updateByWrapper(new AuthRole().setActive(Boolean.TRUE), Pops.<AuthRole>lambdaUpdate()
                .from(AuthRole.MODEL_MODEL)
                .eq(AuthRole::getId, id)
                .eq(AuthRole::getActive, Boolean.FALSE)
                .ne(AuthRole::getSource, AuthorizationSourceEnum.BUILD_IN));
        if (effectRow == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean disable(Long id) {
        Integer effectRow = updateByWrapper(new AuthRole().setActive(Boolean.FALSE), Pops.<AuthRole>lambdaUpdate()
                .from(AuthRole.MODEL_MODEL)
                .eq(AuthRole::getId, id)
                .eq(AuthRole::getActive, Boolean.TRUE)
                .ne(AuthRole::getSource, AuthorizationSourceEnum.BUILD_IN));
        if (effectRow == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    protected AuthRole verificationAndSet(AuthRole data, boolean isUpdate) {
        if (isUpdate) {
            AuthRole origin = queryOne(data);
            if (origin == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
            }
            data.setId(origin.getId());
            data.setCode(origin.getCode());
            data.setSource(origin.getSource());
            data.setActive(origin.getActive());

            verifyName(data.getName(), origin);
        } else {
            String code = data.getCode();
            if (StringUtils.isBlank(code)) {
                code = generatorCode();
                data.setCode(code);
            } else {
                verifyCode(code);
            }
            verifyName(data.getName(), null);
            VerificationHelper.setDefaultValue(data, AuthRole::getSource, AuthRole::setSource, AuthorizationSourceEnum.MANUAL);
            VerificationHelper.setDefaultValue(data, AuthRole::getActive, AuthRole::setActive, Boolean.TRUE);
        }
        return data;
    }

    private String generatorCode() {
        String code = UUIDUtil.getUUIDNumberString();
        while (Models.origin().count(Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .eq(AuthRole::getCode, code)).compareTo(1L) >= 0) {
            code = UUIDUtil.getUUIDNumberString();
        }
        return code;
    }

    private void verifyCode(String code) {
        if (code.length() > 64) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_CODE_TOO_LONG_ERROR).errThrow();
        }
        if (Models.origin().count(Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .eq(AuthRole::getCode, code)).compareTo(1L) >= 0) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_CODE_EXISTS_ERROR).errThrow();
        }
    }

    private void verifyName(String name, AuthRole origin) {
        if (origin != null && origin.getName().equals(name)) {
            return;
        }
        if (StringUtils.isBlank(name)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_NAME_IS_NULL_ERROR).errThrow();
        }
        if (name.length() > 128) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_NAME_TOO_LONG_ERROR).errThrow();
        }
        LambdaQueryWrapper<AuthRole> wrapper = Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .eq(AuthRole::getName, name);
        if (origin != null) {
            wrapper.ne(AuthRole::getId, origin.getId());
        }
        if (Models.origin().count(wrapper).compareTo(1L) >= 0) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_NAME_EXISTS_ERROR).errThrow();
        }
    }
}
