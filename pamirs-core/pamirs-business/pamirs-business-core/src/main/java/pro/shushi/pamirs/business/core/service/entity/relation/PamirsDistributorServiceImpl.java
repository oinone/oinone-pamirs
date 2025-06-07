package pro.shushi.pamirs.business.core.service.entity.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.relation.PamirsDistributor;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.service.entity.relation.PamirsDistributorService;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsDistributorService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsDistributorService.FUN_NAMESPACE)
public class PamirsDistributorServiceImpl extends AbstractPamirsPartnerRelationService<PamirsDistributor> implements PamirsDistributorService {

    @Function
    @Override
    public PamirsDistributor create(PamirsDistributor data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsDistributor> createBatch(List<PamirsDistributor> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsDistributor update(PamirsDistributor data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsDistributor createOrUpdate(PamirsDistributor data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsDistributor> delete(List<PamirsDistributor> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsDistributor deleteOne(PamirsDistributor data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsDistributor> queryPage(Pagination<PamirsDistributor> page, LambdaQueryWrapper<PamirsDistributor> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsDistributor queryOne(PamirsDistributor query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsDistributor queryOneByWrapper(LambdaQueryWrapper<PamirsDistributor> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsDistributor> queryListByWrapper(LambdaQueryWrapper<PamirsDistributor> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsDistributor> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected String getRelationType() {
        return PamirsDistributor.RELATION_TYPE;
    }

    @Override
    protected PamirsPartner getOriginPartner(PamirsDistributor data) {
        return data.getOriginPartner();
    }

    @Override
    protected PamirsPartner getTargetPartner(PamirsDistributor data) {
        return data.getTargetPartner();
    }

    @Override
    protected PamirsDistributor realUpdate(PamirsDistributor data) {
        return PropertyHelper.<PamirsDistributor, PamirsDistributor>ignoreProperty(data)
                .oops(PamirsDistributor::getCode, PamirsDistributor::setCode, PamirsDistributor::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public PamirsDistributor queryById(Long id) {
        return new PamirsDistributor().queryById(id);
    }

    @Function
    @Override
    public Boolean deleteByCode(String code) {
        return new PamirsDistributor().deleteByCode(code);
    }

}
