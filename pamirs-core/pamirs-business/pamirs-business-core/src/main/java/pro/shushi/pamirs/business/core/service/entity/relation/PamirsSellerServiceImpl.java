package pro.shushi.pamirs.business.core.service.entity.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.relation.PamirsSeller;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.service.entity.relation.PamirsSellerService;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsSellerService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsSellerService.FUN_NAMESPACE)
public class PamirsSellerServiceImpl extends AbstractPamirsPartnerRelationService<PamirsSeller> implements PamirsSellerService {

    @Function
    @Override
    public PamirsSeller create(PamirsSeller data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsSeller> createBatch(List<PamirsSeller> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsSeller update(PamirsSeller data) {
        data = verificationAndSet(data, true);
        afterProperties(data, true);
        return data;
    }

    @Function
    @Override
    public PamirsSeller createOrUpdate(PamirsSeller data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsSeller> delete(List<PamirsSeller> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsSeller deleteOne(PamirsSeller data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsSeller> queryPage(Pagination<PamirsSeller> page, LambdaQueryWrapper<PamirsSeller> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsSeller queryOne(PamirsSeller query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsSeller queryOneByWrapper(LambdaQueryWrapper<PamirsSeller> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsSeller> queryListByWrapper(LambdaQueryWrapper<PamirsSeller> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsSeller> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected String getRelationType() {
        return PamirsSeller.RELATION_TYPE;
    }

    @Override
    protected PamirsPartner getOriginPartner(PamirsSeller data) {
        return data.getOriginPartner();
    }

    @Override
    protected PamirsPartner getTargetPartner(PamirsSeller data) {
        return data.getTargetPartner();
    }

    @Override
    protected PamirsSeller realUpdate(PamirsSeller data) {
        return PropertyHelper.<PamirsSeller, PamirsSeller>ignoreProperty(data)
                .oops(PamirsSeller::getCode, PamirsSeller::setCode, PamirsSeller::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public PamirsSeller queryById(Long id){
        return new PamirsSeller().queryById(id);
    }
}
