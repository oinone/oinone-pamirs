package pro.shushi.pamirs.business.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.business.api.service.PamirsPositionService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

/**
 * {@link PamirsPositionService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsPositionService.FUN_NAMESPACE)
public class PamirsPositionServiceImpl implements PamirsPositionService {

    @Function
    @Override
    public PamirsPosition create(PamirsPosition data) {
//        if (StringUtils.isBlank(data.getCompanyCode())) {
//            throw PamirsException.construct(BusinessExpEnumerate.COMPANY_CODE_EMPTY).errThrow();
//        }
        data = data.create();
        return data;
    }

    @Function
    @Override
    public void updateByPk(PamirsPosition data) {
        data.updateByPk();
    }

    @Function
    @Override
    public void deleteByPks(List<PamirsPosition> list) {
        new PamirsPosition().deleteByPks(list);
    }

    @Function
    @Override
    public void deleteByPk(PamirsPosition data) {
        data.deleteByPk();
    }

    @Function
    @Override
    public Pagination<PamirsPosition> queryPage(Pagination<PamirsPosition> page, IWrapper<PamirsPosition> queryWrapper) {
        return new PamirsPosition().queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsPosition queryOne(PamirsPosition query) {
        return query.queryOne();
    }
}
