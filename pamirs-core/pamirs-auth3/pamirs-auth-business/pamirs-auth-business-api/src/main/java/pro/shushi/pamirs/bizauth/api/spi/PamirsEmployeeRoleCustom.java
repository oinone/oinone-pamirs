package pro.shushi.pamirs.bizauth.api.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.spi.CurrentRolesFetcher;
import pro.shushi.pamirs.bizauth.api.session.BusinessCodeSession;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order
@Component
@SPI.Service("PamirsEmployeeRoleCustom")
public class PamirsEmployeeRoleCustom implements CurrentRolesFetcher {

    @Autowired
    private PamirsEmployeeService pamirsEmployeeService;

    /**
     * 通过用户id以及公司code查询到对应员工的角色信息
     *
     * @return roleIds
     */
    @Override
    public Set<Long> fetch() {
        Set<Long> roleIds = new LinkedHashSet<>();
        Long userId = PamirsSession.getUserId();
        if (null == userId) {
            return roleIds;
        }
        String code = BusinessCodeSession.getCode();

        //通过用户id拿到员工列表
        List<PamirsEmployee> pamirsEmployeeList = pamirsEmployeeService.queryListByUid(userId);
        if (CollectionUtils.isEmpty(pamirsEmployeeList)) {
            return roleIds;
        }
        if (StringUtils.isBlank(code)) {
            PamirsEmployee pamirsEmployee = pamirsEmployeeList.get(0).fieldQuery(PamirsEmployee::getRoles);
            if (CollectionUtils.isNotEmpty(pamirsEmployee.getRoles())) {
                roleIds.addAll(pamirsEmployee.getRoles().stream().map(v -> v.getId()).collect(Collectors.toList()));
            }
            return roleIds;
        }

        //通过code拿到对应到员工
        pamirsEmployeeList.stream()
                .filter(employee -> code.equals(employee.getCompanyCode()))
                .findFirst()
                .ifPresent(employee -> {
                    // 通过这个员工拿到对应到角色
                    PamirsEmployee pamirsEmployee = employee.fieldQuery(PamirsEmployee::getRoles);
                    if (CollectionUtils.isNotEmpty(pamirsEmployee.getRoles())) {
                        roleIds.addAll(pamirsEmployee.getRoles().stream().map(v -> v.getId()).collect(Collectors.toList()));
                    }
                });
        return roleIds;
    }
}