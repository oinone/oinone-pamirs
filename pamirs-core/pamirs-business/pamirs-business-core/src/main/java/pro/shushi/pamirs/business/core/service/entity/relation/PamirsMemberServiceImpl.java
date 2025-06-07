package pro.shushi.pamirs.business.core.service.entity.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.relation.PamirsMember;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.service.entity.relation.PamirsMemberService;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsMemberService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsMemberService.FUN_NAMESPACE)
public class PamirsMemberServiceImpl extends AbstractPamirsPartnerRelationService<PamirsMember> implements PamirsMemberService {

    @Function
    @Override
    public PamirsMember create(PamirsMember data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsMember> createBatch(List<PamirsMember> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsMember update(PamirsMember data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsMember createOrUpdate(PamirsMember data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsMember> delete(List<PamirsMember> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsMember deleteOne(PamirsMember data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsMember> queryPage(Pagination<PamirsMember> page, LambdaQueryWrapper<PamirsMember> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsMember queryOne(PamirsMember query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsMember queryOneByWrapper(LambdaQueryWrapper<PamirsMember> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsMember> queryListByWrapper(LambdaQueryWrapper<PamirsMember> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsMember> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected String getRelationType() {
        return PamirsMember.RELATION_TYPE;
    }

    @Override
    protected PamirsPartner getOriginPartner(PamirsMember data) {
        return data.getOriginPartner();
    }

    @Override
    protected PamirsPartner getTargetPartner(PamirsMember data) {
        return data.getTargetPartner();
    }

    @Override
    protected PamirsMember realUpdate(PamirsMember data) {
        return PropertyHelper.<PamirsMember, PamirsMember>ignoreProperty(data)
                .oops(PamirsMember::getCode, PamirsMember::setCode, PamirsMember::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public PamirsMember queryById(Long id) {
        return new PamirsMember().queryById(id);
    }
}
