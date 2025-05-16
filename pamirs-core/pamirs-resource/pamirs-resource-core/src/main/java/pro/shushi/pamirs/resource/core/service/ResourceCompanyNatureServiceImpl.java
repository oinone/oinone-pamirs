package pro.shushi.pamirs.resource.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.model.ResourceCompanyNature;
import pro.shushi.pamirs.resource.api.service.ResourceCompanyNatureService;

@Service
@Fun(ResourceCompanyNatureService.FUN_NAMESPACE)
public class ResourceCompanyNatureServiceImpl implements ResourceCompanyNatureService {

    @Function
    @Override
    public ResourceCompanyNature update(ResourceCompanyNature data) {
        verificationAndSet(data, true);
        data.updateById();
        return data;
    }

    @Function
    @Override
    public ResourceCompanyNature create(ResourceCompanyNature data) {
        verificationAndSet(data, false);
        data.create();
        return data;
    }

    private void verificationAndSet(ResourceCompanyNature data, boolean isUpdate) {
        ResourceCompanyNature origin;
        Long id = null;
        if (isUpdate) {
            origin = FetchUtil.fetchOne(data);
            if (origin == null)
                throw PamirsException.construct(ExpEnumerate.SELECT_NULL).errThrow();
            id = origin.getId();
        }
        //编码校验全局唯一
        String code = data.getCode();
        if (StringUtils.isBlank(code))
            throw PamirsException.construct(ExpEnumerate.RESOURCE_COMPANY_CODE_ISNULL).errThrow();
        LambdaQueryWrapper<ResourceCompanyNature> wrapper = Pops.<ResourceCompanyNature>lambdaQuery()
                .from(ResourceCompanyNature.MODEL_MODEL)
                .eq(ResourceCompanyNature::getCode, code);
        if (isUpdate)
            wrapper.ne(ResourceCompanyNature::getId, id);
        Long count = data.count(wrapper);
        if (count >= 1)
            throw PamirsException.construct(ExpEnumerate.RESOURCE_COMPANY_CODE_ISEXIST).errThrow();
    }
}
