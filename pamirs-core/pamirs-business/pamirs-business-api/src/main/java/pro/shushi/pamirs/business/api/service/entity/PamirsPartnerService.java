package pro.shushi.pamirs.business.api.service.entity;

import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsPartner}服务
 * * 不该有这种服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsPartnerService.FUN_NAMESPACE)
public interface PamirsPartnerService extends StandardModelService<PamirsPartner> {

    String FUN_NAMESPACE = "business.PamirsPartnerService";

    @Function
    @Override
    PamirsPartner create(PamirsPartner data);

    @Function
    @Override
    PamirsPartner update(PamirsPartner data);

    @Function
    @Override
    List<PamirsPartner> delete(List<PamirsPartner> list);

    @Function
    @Override
    PamirsPartner deleteOne(PamirsPartner data);

    @Function
    @Override
    Pagination<PamirsPartner> queryPage(Pagination<PamirsPartner> page, LambdaQueryWrapper<PamirsPartner> queryWrapper);

    @Function
    @Override
    PamirsPartner queryOne(PamirsPartner query);

    @Function
    @Override
    PamirsPartner queryOneByWrapper(LambdaQueryWrapper<PamirsPartner> queryWrapper);

    @Function
    @Override
    List<PamirsPartner> queryListByWrapper(LambdaQueryWrapper<PamirsPartner> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsPartner> queryWrapper);

    /**
     * 根据code查询接口
     *
     * @param code
     * @return
     */
    @Function
    PamirsPartner queryByCode(String code);

    /**
     * 根据codes查询接口
     *
     * @param codes
     * @return
     */
    @Function
    List<PamirsPartner> queryByCodes(List<String> codes);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    PamirsPartner queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<PamirsPartner> queryByIds(List<Long> ids);

    @Function
    Integer updateById(PamirsPartner data);
}
