package pro.shushi.pamirs.business.core.service.entity;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.business.api.service.entity.PamirsCompanyService;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsCompanyService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsCompanyService.FUN_NAMESPACE)
public class PamirsCompanyServiceImpl extends AbstractPamirsPartnerService<PamirsCompany> implements PamirsCompanyService {

    @Function
    @Override
    public PamirsCompany create(PamirsCompany data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsCompany> createBatch(List<PamirsCompany> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsCompany update(PamirsCompany data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsCompany createOrUpdate(PamirsCompany data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsCompany> delete(List<PamirsCompany> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsCompany deleteOne(PamirsCompany data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsCompany> queryPage(Pagination<PamirsCompany> page, LambdaQueryWrapper<PamirsCompany> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsCompany queryOne(PamirsCompany query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsCompany queryOneByWrapper(LambdaQueryWrapper<PamirsCompany> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsCompany> queryListByWrapper(LambdaQueryWrapper<PamirsCompany> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsCompany> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected BusinessPartnerTypeEnum getPartnerType() {
        return BusinessPartnerTypeEnum.COMPANY;
    }

    @Override
    @Function
    protected PamirsCompany realUpdate(PamirsCompany data) {
        return PropertyHelper.<PamirsCompany, PamirsCompany>ignoreProperty(data)
                .oops(PamirsCompany::getCode, PamirsCompany::setCode, PamirsCompany::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public void deleteList(List<PamirsCompany> list) {
        new PamirsCompany().deleteByPks(list);
    }

    @Override
    @Function
    public void updateById(PamirsCompany data) {
        data.updateById();
        data.fieldSave(PamirsCompany::getParent);
        data.fieldSave(PamirsCompany::getRegisterAddress);
    }

    @Override
    @Function
    public PamirsCompany queryById(Long id) {
        return new PamirsCompany().queryById(id);
    }

}
