package pro.shushi.pamirs.business.core.service.entity;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.service.entity.PamirsPartnerService;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsPartnerService}实现
 * * 不该有这种服务
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsPartnerService.FUN_NAMESPACE)
public class PamirsPartnerServiceImpl extends AbstractPamirsPartnerService<PamirsPartner> implements PamirsPartnerService {

    @Function
    @Override
    public PamirsPartner create(PamirsPartner data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsPartner> createBatch(List<PamirsPartner> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsPartner update(PamirsPartner data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsPartner createOrUpdate(PamirsPartner data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsPartner> delete(List<PamirsPartner> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsPartner deleteOne(PamirsPartner data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsPartner> queryPage(Pagination<PamirsPartner> page, LambdaQueryWrapper<PamirsPartner> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsPartner queryOne(PamirsPartner query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsPartner queryOneByWrapper(LambdaQueryWrapper<PamirsPartner> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsPartner> queryListByWrapper(LambdaQueryWrapper<PamirsPartner> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsPartner> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected BusinessPartnerTypeEnum getPartnerType() {
        return BusinessPartnerTypeEnum.NONE;
    }

    @Override
    protected PamirsPartner realUpdate(PamirsPartner data) {
        return PropertyHelper.<PamirsPartner, PamirsPartner>ignoreProperty(data)
                .oops(PamirsPartner::getCode, PamirsPartner::setCode, PamirsPartner::unsetCode)
                .execute(this::rawUpdate);
    }

    @Override
    @Function
    public PamirsPartner queryByCode(String code) {
        return new PamirsPartner().queryByCode(code);
    }

    @Override
    @Function
    public List<PamirsPartner> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(PamirsPartner.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public PamirsPartner queryById(Long id) {
        return new PamirsPartner().queryById(id);
    }

    @Override
    @Function
    public List<PamirsPartner> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(PamirsPartner.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public Integer updateById(PamirsPartner data) {
        return data.updateById();
    }
}
