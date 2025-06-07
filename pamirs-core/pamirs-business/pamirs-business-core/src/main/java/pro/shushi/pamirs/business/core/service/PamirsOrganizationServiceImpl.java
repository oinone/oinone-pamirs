package pro.shushi.pamirs.business.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.model.PamirsOrganization;
import pro.shushi.pamirs.business.api.service.PamirsOrganizationService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

/**
 * {@link PamirsOrganizationService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsOrganizationService.FUN_NAMESPACE)
public class PamirsOrganizationServiceImpl implements PamirsOrganizationService {

    @Function
    @Override
    public PamirsOrganization create(PamirsOrganization data) {
        if (StringUtils.isBlank(data.getOrganizationType())) {
            data.setOrganizationType(BusinessModule.DEFAULT_TYPE);
        }
        return data.create();
    }

    @Function
    @Override
    public PamirsOrganization update(PamirsOrganization data) {
        data.updateById();
        return data;
    }

    @Function
    @Override
    public void delete(List<PamirsOrganization> list) {
        new PamirsOrganization().deleteByPks(list);
    }

    @Function
    @Override
    public void deleteOne(PamirsOrganization data) {
        new PamirsOrganization().deleteByPk();
    }

    @Function
    @Override
    public Pagination<PamirsOrganization> queryPage(Pagination<PamirsOrganization> page, IWrapper<PamirsOrganization> queryWrapper) {
        return new PamirsOrganization().queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsOrganization queryOne(PamirsOrganization query) {
        return query.queryOne();
    }
}
