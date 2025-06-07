package pro.shushi.pamirs.business.core.service.entity.relation;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.entity.relation.PamirsRetailer;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.business.api.service.entity.relation.PamirsPartnerRelationService;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.PropertyHelper;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.Wrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.AuditStatusEnum;
import pro.shushi.pamirs.resource.api.model.ResourceMajorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link PamirsPartnerRelationService}实现
 * * 不该有这种服务
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsPartnerRelationService.FUN_NAMESPACE)
public class PamirsPartnerRelationServiceImpl extends AbstractPamirsPartnerRelationService<PamirsPartnerRelation> implements PamirsPartnerRelationService {

    @Function
    @Override
    public PamirsPartnerRelation create(PamirsPartnerRelation data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsPartnerRelation> createBatch(List<PamirsPartnerRelation> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsPartnerRelation update(PamirsPartnerRelation data) {
        return super.update(data);
    }

    @Function
    @Override
    public PamirsPartnerRelation createOrUpdate(PamirsPartnerRelation data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsPartnerRelation> delete(List<PamirsPartnerRelation> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsPartnerRelation deleteOne(PamirsPartnerRelation data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Pagination<PamirsPartnerRelation> queryPage(Pagination<PamirsPartnerRelation> page, LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsPartnerRelation queryOne(PamirsPartnerRelation query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsPartnerRelation queryOneByWrapper(LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsPartnerRelation> queryListByWrapper(LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected String getRelationType() {
        return PamirsPartnerRelation.RELATION_TYPE;
    }

    @Override
    protected PamirsPartner getOriginPartner(PamirsPartnerRelation data) {
        return data.getOriginPartner();
    }

    @Override
    protected PamirsPartner getTargetPartner(PamirsPartnerRelation data) {
        return data.getTargetPartner();
    }

    @Override
    protected PamirsPartnerRelation realUpdate(PamirsPartnerRelation data) {
        return PropertyHelper.<PamirsPartnerRelation, PamirsPartnerRelation>ignoreProperty(data)
                .oops(PamirsPartnerRelation::getCode, PamirsPartnerRelation::setCode, PamirsPartnerRelation::unsetCode)
                .execute(this::rawUpdate);
    }

    @Function
    @Override
    public PamirsPartnerRelation queryById(Long id) {
        return new PamirsPartnerRelation().queryById(id);
    }

    @Function
    @Override
    public Integer updateByCode(PamirsPartnerRelation relation) {
        return relation.updateByCode();
    }

    @Function
    @Override
    public PamirsPartnerRelation queryByCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw PamirsException.construct(BusinessExpEnumerate.CODE_ISNULL_ERROR).errThrow();
        }
        return new PamirsPartnerRelation().queryByCode(code);
    }

    @Function
    @Override
    public PamirsPartnerRelation queryDefaultOriginRelation(String targetPartnerCode) {
        ResourceMajorConfig resourceMajorConfig = new ResourceMajorConfig().singletonModel();
        //fixme tim缓存优化
        List<PamirsPartnerRelation> relations = new PamirsPartnerRelation().queryListByWrapper(
                new Pagination<PamirsPartnerRelation>().setSize(1L),
                Pops.<PamirsPartnerRelation>lambdaQuery()
                        .eq(PamirsPartnerRelation::getTargetPartnerCode, targetPartnerCode)
                        .in(PamirsPartnerRelation::getRelationType, PamirsRetailer.RELATION_TYPE, PamirsRetailer.RELATION_TYPE)
                        .eq(PamirsPartnerRelation::getDataStatus, DataStatusEnum.ENABLED)
                        .eq(PamirsPartnerRelation::getAuditStatus, AuditStatusEnum.SUCCESS)
                        .notIn(PamirsPartnerRelation::getOriginPartnerCode, resourceMajorConfig.getPartnerCode())
                        .from(PamirsPartnerRelation.class));
        if (CollectionUtils.isNotEmpty(relations)) {
            return relations.get(0);
        }
        return null;
    }

    @Function
    @Override
    public List<PamirsPartnerRelation> queryRelationByTargetPartner(String targetPartnerCode, List<String> relationTypes) {
        ResourceMajorConfig resourceMajorConfig = new ResourceMajorConfig().singletonModel();
        return new PamirsPartnerRelation().queryList(Pops.<PamirsPartnerRelation>lambdaQuery()
                .eq(PamirsPartnerRelation::getTargetPartnerCode, targetPartnerCode)
                .in(CollectionUtils.isNotEmpty(relationTypes), PamirsPartnerRelation::getRelationType, relationTypes)
                .eq(PamirsPartnerRelation::getDataStatus, DataStatusEnum.ENABLED)
                .eq(PamirsPartnerRelation::getAuditStatus, AuditStatusEnum.SUCCESS)
                .notIn(PamirsPartnerRelation::getOriginPartnerCode, resourceMajorConfig.getPartnerCode())
                .from(PamirsPartnerRelation.class));
    }

    @Function
    @Override
    public List<PamirsPartnerRelation> queryRelationByTargetPartnerCodes(String originPartnerCode, List<String> targetPartnerCodes) {
        if (CollectionUtils.isEmpty(targetPartnerCodes)) {
            return new ArrayList<>();
        }
        List<List<String>> targetPartnerCodeGroups = pro.shushi.pamirs.core.common.CollectionUtils.fixedGrouping(targetPartnerCodes, 50);
        List<PamirsPartnerRelation> partnerRelations = new ArrayList<>();
        targetPartnerCodeGroups.forEach(onePartnerCodeGroups -> {
            Wrapper<PamirsPartnerRelation> queryWrapper = Pops.<PamirsPartnerRelation>lambdaQuery()
                    .eq(PamirsPartnerRelation::getOriginPartnerCode, originPartnerCode)
                    .eq(PamirsPartnerRelation::getDataStatus, DataStatusEnum.ENABLED)
                    .in(PamirsPartnerRelation::getTargetPartnerCode, onePartnerCodeGroups)
                    .from(PamirsPartnerRelation.MODEL_MODEL);
            List<PamirsPartnerRelation> onePartnerRelations = new PamirsPartnerRelation().queryList(queryWrapper);
            if (CollectionUtils.isNotEmpty(onePartnerRelations)) {
                partnerRelations.addAll(onePartnerRelations);
            }
        });
        return partnerRelations;
    }

    @Function
    @Override
    public PamirsPartnerRelation queryActiveRelationByPartnerCodes(String originPartnerCode, String targetPartnerCode) {
        Wrapper<PamirsPartnerRelation> queryWrapper = Pops.<PamirsPartnerRelation>lambdaQuery()
                .eq(PamirsPartnerRelation::getOriginPartnerCode, originPartnerCode)
                .eq(PamirsPartnerRelation::getTargetPartnerCode, targetPartnerCode)
                .eq(PamirsPartnerRelation::getDataStatus, DataStatusEnum.ENABLED)
                .from(PamirsPartnerRelation.MODEL_MODEL);
        List<PamirsPartnerRelation> relations = new PamirsPartnerRelation().queryList(queryWrapper);
        if (CollectionUtils.isNotEmpty(relations)) {
            return relations.get(0);
        }
        return null;
    }

    @Function
    @Override
    public List<PamirsPartnerRelation> queryRelationByOriginPartnerCode(String originPartnerCode) {
        Wrapper<PamirsPartnerRelation> queryWrapper = Pops.<PamirsPartnerRelation>lambdaQuery()
                .eq(PamirsPartnerRelation::getOriginPartnerCode, originPartnerCode)
                .from(PamirsPartnerRelation.class);
        return new PamirsPartnerRelation().queryList(queryWrapper);
    }

    // TODO
    @Function
    @Override
    public List<PamirsPartner> queryOriginByTargetPartner(String targetPartnerCode, List<String> relationTypes) {
        List<PamirsPartner> pamirsPartners = new ArrayList<>();
        List<PamirsPartnerRelation> relations = queryRelationByTargetPartner(targetPartnerCode, relationTypes);
        if (CollectionUtils.isNotEmpty(relations)) {
            List<String> codes = relations.stream().map(PamirsPartnerRelation::getOriginPartnerCode)
                    .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            pamirsPartners = new PamirsPartner().queryList(Pops.<PamirsPartner>lambdaQuery().in(PamirsPartner::getCode, codes).from(PamirsPartner.MODEL_MODEL));
        }
        return pamirsPartners;
    }

    @Function
    @Override
    public Boolean deleteRelationByCode(String code) {
        return new PamirsPartnerRelation().deleteByCode(code);
    }

    @Override
    public Pagination<PamirsPartnerRelation> queryRelationByOriginPartnerCodePage(String originPartnerCode, String keyword, Pagination<PamirsPartnerRelation> page) {
        Wrapper<PamirsPartnerRelation> queryWrapper = Pops.<PamirsPartnerRelation>lambdaQuery()
                .eq(PamirsPartnerRelation::getOriginPartnerCode, originPartnerCode)
                .eq(PamirsPartnerRelation::getDataStatus, DataStatusEnum.ENABLED)
                .and(StringUtils.isNotBlank(keyword), a ->
                        a.like(PamirsPartnerRelation::getTargetPartnerCode, keyword)
                                .or()
                                .like(PamirsPartnerRelation::getName, keyword))
                .orderByDesc(PamirsPartnerRelation::getCreateDate)
                .from(PamirsPartnerRelation.class);
        return new PamirsPartnerRelation().queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public List<PamirsPartnerRelation> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(PamirsPartnerRelation.MODEL_MODEL, codes);
    }

    @Function
    @Override
    public Map<Long, String> queryNameMapByIds(List<Long> relationIds) {
        return pro.shushi.pamirs.core.common.CollectionUtils.fetchNameMapById(relationIds, PamirsPartnerRelation.class);
    }

    @Function
    @Override
    public Boolean retriedRelation(String partnerCode) {
        return null;
    }
}
