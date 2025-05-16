package pro.shushi.pamirs.business.api.service.entity.relation;

import pro.shushi.pamirs.business.api.entity.relation.PamirsMember;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsMember}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsMemberService.FUN_NAMESPACE)
public interface PamirsMemberService extends StandardModelService<PamirsMember> {

    String FUN_NAMESPACE = "business.PamirsMemberService";

    @Function
    @Override
    PamirsMember create(PamirsMember data);

    @Function
    @Override
    PamirsMember update(PamirsMember data);

    @Function
    @Override
    List<PamirsMember> delete(List<PamirsMember> list);

    @Function
    @Override
    PamirsMember deleteOne(PamirsMember data);

    @Function
    @Override
    Pagination<PamirsMember> queryPage(Pagination<PamirsMember> page, LambdaQueryWrapper<PamirsMember> queryWrapper);

    @Function
    @Override
    PamirsMember queryOne(PamirsMember query);

    @Function
    @Override
    PamirsMember queryOneByWrapper(LambdaQueryWrapper<PamirsMember> queryWrapper);

    @Function
    @Override
    List<PamirsMember> queryListByWrapper(LambdaQueryWrapper<PamirsMember> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsMember> queryWrapper);

    @Function
    PamirsMember queryById(Long id);
}
