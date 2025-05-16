package pro.shushi.pamirs.bizauth.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.bizauth.api.cache.entity.BusinessCodeCacheKey;
import pro.shushi.pamirs.bizauth.api.cache.service.BusinessCodeUserCacheService;
import pro.shushi.pamirs.bizauth.api.service.BusinessEmployeeService;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.model.relation.EmployeeRelRole;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Fun(BusinessEmployeeService.FUN_NAMESPACE)
public class BusinessEmployeeServiceImpl implements BusinessEmployeeService {

    @Autowired
    private BusinessCodeUserCacheService businessCodeUserCacheService;

    @Autowired
    private PamirsEmployeeService pamirsEmployeeService;

    @Override
    public Boolean bindEmployeeRole(List<AuthRole> roleList, List<PamirsEmployee> employeeList) {

        if (CollectionUtils.isEmpty(employeeList) || CollectionUtils.isEmpty(roleList)) {
            return Boolean.FALSE;
        }
        List<Long> employeeIdList = employeeList.stream().map(IdModel::getId).collect(Collectors.toList());
        List<Long> roleIdList = roleList.stream().map(IdModel::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(employeeIdList) || CollectionUtils.isEmpty(roleIdList)) {
            return Boolean.FALSE;
        }

        List<EmployeeRelRole> employeeRoleRels = Models.origin().queryListByWrapper(Pops.<EmployeeRelRole>lambdaQuery()
                .from(EmployeeRelRole.MODEL_MODEL)
                .in(EmployeeRelRole::getEmployeeId, employeeIdList)
                .in(EmployeeRelRole::getAuthRoleId, roleIdList));

        Set<String> existKeys = employeeRoleRels.stream()
                .map(i -> i.getEmployeeId() + "-" + i.getAuthRoleId())
                .collect(Collectors.toSet());
        List<EmployeeRelRole> insertEntities = new ArrayList<>();

        Set<Long> createRoleIds = new HashSet<>();
        for (PamirsEmployee employee : employeeList) {
            Long employeeId = employee.getId();
            String companyCode = pamirsEmployeeService.queryById(employeeId).getCompanyCode();
            Long userId = employee.getBindingUserId();
            for (Long roleId : roleIdList) {
                if (existKeys.contains(employeeId + "-" + roleId)) {
                    continue;
                }
                insertEntities.add(new EmployeeRelRole().setEmployeeId(employeeId).setAuthRoleId(roleId));
                createRoleIds.add(roleId);
            }
            //插入绑定关系,刷新缓存
            Models.origin().createBatch(insertEntities);
            businessCodeUserCacheService.add(new BusinessCodeCacheKey(userId,companyCode),createRoleIds);
        }
        return Boolean.TRUE;
    }
}