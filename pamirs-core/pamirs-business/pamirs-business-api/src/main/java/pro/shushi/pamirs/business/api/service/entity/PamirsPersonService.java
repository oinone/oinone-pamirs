package pro.shushi.pamirs.business.api.service.entity;

import pro.shushi.pamirs.business.api.entity.PamirsPerson;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsPerson}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsPersonService.FUN_NAMESPACE)
public interface PamirsPersonService extends StandardModelService<PamirsPerson> {

    String FUN_NAMESPACE = "business.PamirsPersonService";

    @Function
    @Override
    PamirsPerson create(PamirsPerson data);

    @Function
    @Override
    PamirsPerson update(PamirsPerson data);

    @Function
    @Override
    List<PamirsPerson> delete(List<PamirsPerson> list);

    @Function
    @Override
    PamirsPerson deleteOne(PamirsPerson data);

    @Function
    @Override
    Pagination<PamirsPerson> queryPage(Pagination<PamirsPerson> page, LambdaQueryWrapper<PamirsPerson> queryWrapper);

    @Function
    @Override
    PamirsPerson queryOne(PamirsPerson query);

    @Function
    @Override
    PamirsPerson queryOneByWrapper(LambdaQueryWrapper<PamirsPerson> queryWrapper);

    @Function
    @Override
    List<PamirsPerson> queryListByWrapper(LambdaQueryWrapper<PamirsPerson> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsPerson> queryWrapper);

    @Function
    PamirsPerson queryById(Long id);
}
