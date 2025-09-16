package pro.shushi.pamirs.business.api.service;

import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;


@Fun(PamirsDepartmentService.FUN_NAMESPACE)
public interface PamirsDepartmentService {

    String FUN_NAMESPACE = "business.PamirsDepartmentService";

    @Function
    PamirsDepartment create(PamirsDepartment data);

    @Function
    PamirsDepartment queryOne(PamirsDepartment data);

    @Function
    void update(PamirsDepartment data);

    @Function
    void deleteByPk(PamirsDepartment data);

    @Function
    void deleteByPks(List<PamirsDepartment> list);

    @Function
    Pagination<PamirsDepartment> queryPage(Pagination<PamirsDepartment> page, IWrapper<PamirsDepartment> queryWrapper);

    @Function
    Pagination<PamirsDepartment> queryPageAndFillSupervisor(Pagination<PamirsDepartment> page, IWrapper<PamirsDepartment> queryWrapper);

    @Function
    PamirsDepartment fillDeptSupervisor(PamirsDepartment department);

    @Function
    List<PamirsDepartment> queryDepartmentRootList(IWrapper<PamirsDepartment> queryWrapper);
}
