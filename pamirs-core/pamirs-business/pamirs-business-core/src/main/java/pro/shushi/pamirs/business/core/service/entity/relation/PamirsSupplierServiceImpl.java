package pro.shushi.pamirs.business.core.service.entity.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.relation.PamirsSupplier;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.service.entity.relation.PamirsSupplierService;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsSupplierService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsSupplierService.FUN_NAMESPACE)
public class PamirsSupplierServiceImpl extends AbstractPamirsPartnerRelationService<PamirsSupplier> implements PamirsSupplierService {

    @Function
    @Override
    public PamirsSupplier create(PamirsSupplier data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsSupplier> createBatch(List<PamirsSupplier> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsSupplier update(PamirsSupplier data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsSupplier createOrUpdate(PamirsSupplier data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsSupplier> delete(List<PamirsSupplier> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsSupplier deleteOne(PamirsSupplier data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsSupplier> queryPage(Pagination<PamirsSupplier> page, LambdaQueryWrapper<PamirsSupplier> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsSupplier queryOne(PamirsSupplier query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsSupplier queryOneByWrapper(LambdaQueryWrapper<PamirsSupplier> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsSupplier> queryListByWrapper(LambdaQueryWrapper<PamirsSupplier> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsSupplier> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected String getRelationType() {
        return PamirsSupplier.RELATION_TYPE;
    }

    @Override
    protected PamirsPartner getOriginPartner(PamirsSupplier data) {
        return data.getOriginPartner();
    }

    @Override
    protected PamirsPartner getTargetPartner(PamirsSupplier data) {
        return data.getTargetPartner();
    }

    @Override
    protected PamirsSupplier realUpdate(PamirsSupplier data) {
        return PropertyHelper.<PamirsSupplier, PamirsSupplier>ignoreProperty(data)
                .oops(PamirsSupplier::getCode, PamirsSupplier::setCode, PamirsSupplier::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public PamirsSupplier queryById(Long id){
        return new PamirsSupplier().queryById(id);
    }
}
