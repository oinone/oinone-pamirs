package pro.shushi.pamirs.business.api.service.entity.relation;

import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Map;

/**
 * {@link PamirsPartnerRelation}服务
 * * 不该有这种服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsPartnerRelationService.FUN_NAMESPACE)
public interface PamirsPartnerRelationService extends StandardModelService<PamirsPartnerRelation> {

    String FUN_NAMESPACE = "business.PamirsPartnerRelationService";

    @Function
    @Override
    PamirsPartnerRelation create(PamirsPartnerRelation data);

    @Function
    @Override
    PamirsPartnerRelation update(PamirsPartnerRelation data);

    @Function
    @Override
    List<PamirsPartnerRelation> delete(List<PamirsPartnerRelation> list);

    @Function
    @Override
    PamirsPartnerRelation deleteOne(PamirsPartnerRelation data);

    @Function
    @Override
    Pagination<PamirsPartnerRelation> queryPage(Pagination<PamirsPartnerRelation> page, LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper);

    @Function
    @Override
    PamirsPartnerRelation queryOne(PamirsPartnerRelation query);

    @Function
    @Override
    PamirsPartnerRelation queryOneByWrapper(LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper);

    @Function
    @Override
    List<PamirsPartnerRelation> queryListByWrapper(LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsPartnerRelation> queryWrapper);

    @Function
    PamirsPartnerRelation queryById(Long id);

    @Function
    Integer updateByCode(PamirsPartnerRelation relation);

    @Function
    PamirsPartnerRelation queryByCode(String code);

    /**
     * 查询默认的origin合作关系
     * relationType为经销商、门店
     *
     * @param targetPartnerCode
     */
    @Function
    PamirsPartnerRelation queryDefaultOriginRelation(String targetPartnerCode);

    /**
     * 根据target查询关系
     *
     * @param targetPartnerCode
     * @param relationTypes     非必传，传了会按照relationType查询
     */
    @Function
    List<PamirsPartnerRelation> queryRelationByTargetPartner(String targetPartnerCode, List<String> relationTypes);

    @Function
    List<PamirsPartnerRelation> queryRelationByTargetPartnerCodes(String originPartnerCode, List<String> targetPartnerCodes);

    @Function
    PamirsPartnerRelation queryActiveRelationByPartnerCodes(String originPartnerCode, String targetPartnerCode);

    @Function
    List<PamirsPartnerRelation> queryRelationByOriginPartnerCode(String originPartnerCode);

    @Function
    List<PamirsPartner> queryOriginByTargetPartner(String targetPartnerCode, List<String> relationTypes);

    @Function
    Boolean deleteRelationByCode(String code);

    @Function
    Pagination<PamirsPartnerRelation> queryRelationByOriginPartnerCodePage(String originPartnerCode, String keyword, Pagination<PamirsPartnerRelation> page);

    /*OMS所需接口*/
    @Function
    List<PamirsPartnerRelation> queryByCodes(List<String> codes);

    @Function
    Map<Long, String> queryNameMapByIds(List<Long> relationIds);

    @Function
    public Boolean retriedRelation(String partnerCode);
}
