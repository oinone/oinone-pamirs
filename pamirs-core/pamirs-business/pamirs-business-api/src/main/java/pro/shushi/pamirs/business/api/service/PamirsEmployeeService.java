package pro.shushi.pamirs.business.api.service;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

/**
 * {@link PamirsEmployee}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsEmployeeService.FUN_NAMESPACE)
public interface PamirsEmployeeService {

    public static final String SUPER_ADMIN = "超级管理员";

    String FUN_NAMESPACE = "business.PamirsEmployeeService";

    @Function
    PamirsEmployee create(PamirsEmployee t);

    @Function
    PamirsEmployee createEmployeeAndUser(PamirsEmployee t);

    @Function
    PamirsEmployee update(PamirsEmployee data);

    @Function
    PamirsEmployee updateById(PamirsEmployee data);

    @Function
    void deleteByPks(List<PamirsEmployee> list);

    @Function
    void deleteById(PamirsEmployee data);

    @Function
    Pagination<PamirsEmployee> queryPage(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper);

    @Function
    PamirsEmployee queryOne(PamirsEmployee query);

    /**
     * 查询用户作为员工在所有公司主体下的角色并集
     *
     * @param userId
     * @return
     */
    @Function
    List<AuthRole> queryEmployeeRoleListByUid(Long userId);

    @Function
    List<AuthRole> queryEmployeeRoleListByEmployeeId(Long employeeId);

    @Function
    List<PamirsEmployee> queryListByUid(Long userId);

    @Function
    PamirsEmployee queryById(Long employeeId);

    @Function
    Pagination<PamirsEmployee> queryPageImmediateSupervisor(Pagination<PamirsEmployee> page, IWrapper<PamirsEmployee> queryWrapper);
}
