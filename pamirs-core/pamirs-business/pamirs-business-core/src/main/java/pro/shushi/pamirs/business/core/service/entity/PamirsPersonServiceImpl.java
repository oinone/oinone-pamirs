package pro.shushi.pamirs.business.core.service.entity;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.PamirsPerson;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.business.api.service.entity.PamirsPersonService;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsPersonService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsPersonService.FUN_NAMESPACE)
public class PamirsPersonServiceImpl extends AbstractPamirsPartnerService<PamirsPerson> implements PamirsPersonService {

    @Function
    @Override
    public PamirsPerson create(PamirsPerson data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsPerson> createBatch(List<PamirsPerson> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsPerson update(PamirsPerson data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsPerson createOrUpdate(PamirsPerson data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsPerson> delete(List<PamirsPerson> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsPerson deleteOne(PamirsPerson data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsPerson> queryPage(Pagination<PamirsPerson> page, LambdaQueryWrapper<PamirsPerson> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsPerson queryOne(PamirsPerson query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsPerson queryOneByWrapper(LambdaQueryWrapper<PamirsPerson> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsPerson> queryListByWrapper(LambdaQueryWrapper<PamirsPerson> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsPerson> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected BusinessPartnerTypeEnum getPartnerType() {
        return BusinessPartnerTypeEnum.PERSON;
    }

    @Override
    protected PamirsPerson realUpdate(PamirsPerson data) {
        return PropertyHelper.<PamirsPerson, PamirsPerson>ignoreProperty(data)
                .oops(PamirsPerson::getCode, PamirsPerson::setCode, PamirsPerson::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public PamirsPerson queryById(Long id) {
        return new PamirsPerson().queryById(id);
    }
}
