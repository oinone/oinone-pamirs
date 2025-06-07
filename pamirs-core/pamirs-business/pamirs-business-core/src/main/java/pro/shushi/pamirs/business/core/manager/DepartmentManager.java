package pro.shushi.pamirs.business.core.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR;

@Slf4j
@Component
public class DepartmentManager {

    /**
     * 获取当前登录部门CODE
     *
     * @return
     */
    public String currentUserDeptCode() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            throw PamirsException.construct(BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }

        return userDeptCode(userId);
    }

    /**
     * 获取用户的部门 TREECODE-树编码
     *
     * @return
     */
    public String userDeptCode(Long userId) {
        if (userId == null) {
            log.warn("userCurrent deptTreeCode:-1");
        }

        // TODO:多组织的问题。目前如果一个用户对应多个部门的员工，则从第一个员工中获取
        List<PamirsEmployee> employeeList = new PamirsEmployee()
                .queryList(Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).eq(PamirsEmployee::getBindingUserId, userId));
        if (CollectionUtils.isEmpty(employeeList)) {
            log.warn("userCurrent deptTreeCode:-1");
        }
        String deptTreeCode = employeeList.get(0).getDepartmentTreeCode();
        log.info("userCurrent deptTreeCode:{}", deptTreeCode);

        return deptTreeCode;
    }

    public Set<String> children(String parentCode) {
        Set<String> children = new HashSet<>();
        children(children, parentCode);
        return children;
    }

    public void children(Set<String> children, String code) {
        IWrapper<PamirsDepartment> qw = Pops.<PamirsDepartment>lambdaQuery().from(PamirsDepartment.MODEL_MODEL)
                .eq(PamirsDepartment::getParentCode, code);
        List<PamirsDepartment> departmentList = new PamirsDepartment().queryList(qw);
        for (PamirsDepartment child : departmentList) {
            String childCode = child.getCode();
            if (StringUtils.isBlank(childCode)) {
                continue;
            }
            children.add(childCode);
            children(children, childCode);
        }
    }

    public String parent(String childCode) {
        IWrapper<PamirsDepartment> qw = Pops.<PamirsDepartment>lambdaQuery().from(PamirsDepartment.MODEL_MODEL)
                .eq(PamirsDepartment::getCode, childCode);
        PamirsDepartment department = new PamirsDepartment().queryOneByWrapper(qw);
        String parentCode = department.getParentCode();
        if (StringUtils.isBlank(parentCode)) {
            return childCode;
        } else {
            return parent(parentCode);
        }
    }

}
