package pro.shushi.pamirs.auth.core.service.group;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthCustomGroup;
import pro.shushi.pamirs.auth.api.service.group.AuthCustomGroupService;
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
 * 自定义权限组服务实现
 *
 * @author Adamancy Zhang at 12:09 on 2024-08-09
 */
@Service
@Fun(AuthCustomGroupService.FUN_NAMESPACE)
public class AuthCustomGroupServiceImpl extends AbstractStandardModelService<AuthCustomGroup> implements AuthCustomGroupService {

    @Function
    @Override
    public AuthCustomGroup create(AuthCustomGroup data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthCustomGroup> createBatch(List<AuthCustomGroup> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthCustomGroup update(AuthCustomGroup data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthCustomGroup data, LambdaUpdateWrapper<AuthCustomGroup> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthCustomGroup> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthCustomGroup createOrUpdate(AuthCustomGroup data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthCustomGroup> delete(List<AuthCustomGroup> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthCustomGroup deleteOne(AuthCustomGroup data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthCustomGroup> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthCustomGroup> queryPage(Pagination<AuthCustomGroup> page, LambdaQueryWrapper<AuthCustomGroup> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthCustomGroup queryOne(AuthCustomGroup query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthCustomGroup queryOneByWrapper(LambdaQueryWrapper<AuthCustomGroup> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthCustomGroup> queryListByWrapper(LambdaQueryWrapper<AuthCustomGroup> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthCustomGroup> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected AuthCustomGroup verificationAndSet(AuthCustomGroup data, boolean isUpdate) {
        if (isUpdate) {
            AuthCustomGroup origin = queryOne(data);
            if (origin == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_GROUP_ERROR).errThrow();
            }
            data.setId(origin.getId());
            data.setCode(origin.getCode());
            data.setSource(origin.getSource());
        } else {
            String code = data.getCode();
            if (StringUtils.isBlank(code)) {
                code = generatorCode();
                data.setCode(code);
            } else {
                verifyCode(code);
            }
            VerificationHelper.setDefaultValue(data, AuthCustomGroup::getSource, AuthCustomGroup::setSource, AuthorizationSourceEnum.MANUAL);
        }
        return data;
    }

    private String generatorCode() {
        String code = UUIDUtil.getUUIDNumberString();
        while (Models.origin().count(Pops.<AuthCustomGroup>lambdaQuery()
                .from(AuthCustomGroup.MODEL_MODEL)
                .eq(AuthCustomGroup::getCode, code)).compareTo(1L) >= 0) {
            code = UUIDUtil.getUUIDNumberString();
        }
        return code;
    }

    private void verifyCode(String code) {
        if (code.length() > 64) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_CODE_TOO_LONG_ERROR).errThrow();
        }
        if (Models.origin().count(Pops.<AuthCustomGroup>lambdaQuery()
                .from(AuthCustomGroup.MODEL_MODEL)
                .eq(AuthCustomGroup::getCode, code)).compareTo(1L) >= 0) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_CODE_EXISTS_ERROR).errThrow();
        }
    }
}
