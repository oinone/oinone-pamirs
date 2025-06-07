package pro.shushi.pamirs.business.core.service.entity.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.relation.PamirsRetailer;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.service.entity.relation.PamirsRetailerService;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsRetailerService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsRetailerService.FUN_NAMESPACE)
public class PamirsRetailerServiceImpl extends AbstractPamirsPartnerRelationService<PamirsRetailer> implements PamirsRetailerService {

    @Function
    @Override
    public PamirsRetailer create(PamirsRetailer data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsRetailer> createBatch(List<PamirsRetailer> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsRetailer update(PamirsRetailer data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsRetailer createOrUpdate(PamirsRetailer data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsRetailer> delete(List<PamirsRetailer> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsRetailer deleteOne(PamirsRetailer data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsRetailer> queryPage(Pagination<PamirsRetailer> page, LambdaQueryWrapper<PamirsRetailer> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsRetailer queryOne(PamirsRetailer query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsRetailer queryOneByWrapper(LambdaQueryWrapper<PamirsRetailer> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsRetailer> queryListByWrapper(LambdaQueryWrapper<PamirsRetailer> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsRetailer> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected String getRelationType() {
        return PamirsRetailer.RELATION_TYPE;
    }

    @Override
    protected PamirsPartner getOriginPartner(PamirsRetailer data) {
        return data.getOriginPartner();
    }

    @Override
    protected PamirsPartner getTargetPartner(PamirsRetailer data) {
        return data.getTargetPartner();
    }

    @Override
    protected PamirsRetailer realUpdate(PamirsRetailer data) {
        return PropertyHelper.<PamirsRetailer, PamirsRetailer>ignoreProperty(data)
                .oops(PamirsRetailer::getCode, PamirsRetailer::setCode, PamirsRetailer::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public PamirsRetailer queryById(Long id) {
        return new PamirsRetailer().queryById(id);
    }
}
