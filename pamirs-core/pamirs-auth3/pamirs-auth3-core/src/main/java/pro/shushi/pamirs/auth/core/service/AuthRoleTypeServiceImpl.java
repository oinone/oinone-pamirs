package pro.shushi.pamirs.auth.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRoleType;
import pro.shushi.pamirs.auth.api.service.AuthRoleTypeService;
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

/**
 * 角色类型服务实现
 *
 * @author Adamancy Zhang at 11:50 on 2024-01-08
 */
@Service
@Fun(AuthRoleTypeService.FUN_NAMESPACE)
public class AuthRoleTypeServiceImpl extends AbstractStandardModelService<AuthRoleType> implements AuthRoleTypeService {

    @Function
    @Override
    public AuthRoleType create(AuthRoleType data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthRoleType> createBatch(List<AuthRoleType> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthRoleType update(AuthRoleType data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthRoleType data, LambdaUpdateWrapper<AuthRoleType> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthRoleType> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthRoleType createOrUpdate(AuthRoleType data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthRoleType> delete(List<AuthRoleType> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthRoleType deleteOne(AuthRoleType data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleType> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthRoleType> queryPage(Pagination<AuthRoleType> page, LambdaQueryWrapper<AuthRoleType> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthRoleType queryOne(AuthRoleType query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthRoleType queryOneByWrapper(LambdaQueryWrapper<AuthRoleType> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthRoleType> queryListByWrapper(LambdaQueryWrapper<AuthRoleType> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthRoleType> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected AuthRoleType verificationAndSet(AuthRoleType data, boolean isUpdate) {
        if (isUpdate) {
            AuthRoleType origin = queryOne(data);
            if (origin == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_TYPE_ERROR).errThrow();
            }
            data.setId(origin.getId());
            data.setCode(origin.getCode());
            data.setSource(origin.getSource());

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
            VerificationHelper.setDefaultValue(data, AuthRoleType::getSource, AuthRoleType::setSource, AuthorizationSourceEnum.MANUAL);
        }
        return data;
    }

    private String generatorCode() {
        String code = UUIDUtil.getUUIDNumberString();
        while (Models.origin().count(Pops.<AuthRoleType>lambdaQuery()
                .from(AuthRoleType.MODEL_MODEL)
                .eq(AuthRoleType::getCode, code)).compareTo(1L) >= 0) {
            code = UUIDUtil.getUUIDNumberString();
        }
        return code;
    }

    private void verifyCode(String code) {
        if (code.length() > 64) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_TYPE_CODE_TOO_LONG_ERROR).errThrow();
        }
        if (Models.origin().count(Pops.<AuthRoleType>lambdaQuery()
                .from(AuthRoleType.MODEL_MODEL)
                .eq(AuthRoleType::getCode, code)).compareTo(1L) >= 0) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_TYPE_CODE_EXISTS_ERROR).errThrow();
        }
    }

    private void verifyName(String name, AuthRoleType origin) {
        if (origin != null && origin.getName().equals(name)) {
            return;
        }
        if (StringUtils.isBlank(name)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_TYPE_NAME_IS_NULL_ERROR).errThrow();
        }
        if (name.length() > 128) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_TYPE_NAME_TOO_LONG_ERROR).errThrow();
        }
        LambdaQueryWrapper<AuthRoleType> wrapper = Pops.<AuthRoleType>lambdaQuery()
                .from(AuthRoleType.MODEL_MODEL)
                .eq(AuthRoleType::getName, name);
        if (origin != null) {
            wrapper.ne(AuthRoleType::getId, origin.getId());
        }
        if (Models.origin().count(wrapper).compareTo(1L) >= 0) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_TYPE_NAME_EXISTS_ERROR).errThrow();
        }
    }
}
