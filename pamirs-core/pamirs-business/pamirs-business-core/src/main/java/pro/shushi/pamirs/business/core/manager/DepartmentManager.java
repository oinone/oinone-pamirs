package pro.shushi.pamirs.business.core.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.service.PamirsDepartmentService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR;

@Slf4j
@Component
public class DepartmentManager {

    @Autowired
    private PamirsDepartmentService departmentService;

    /**
     * 获取当前登录部门TREE-CODE
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
     * 获取当前登录用户所有绑定员工部门TREE-CODE
     *
     * @return
     */
    public String currentUserDeptCodes() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            throw PamirsException.construct(BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }

        return userDeptCodes(userId);
    }

    /**
     * 获取当前登录用户所有绑定员工部门及子部门TREE-CODE
     *
     * @return
     */
    public String currentUserDeptWithChildCodes() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            throw PamirsException.construct(BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }

        return userDeptWithChildCodes(userId);
    }

    /**
     * 获取用户的部门 TREE-CODE编码
     *
     * @return
     */
    public String userDeptCode(Long userId) {
        if (userId == null) {
            log.warn("userCurrent deptTreeCode:-1");
        }

        // 多组织的问题。目前如果一个用户对应多个部门的员工，则从第一个员工中获取
        List<PamirsEmployee> employeeList = new PamirsEmployee()
                .queryList(Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).eq(PamirsEmployee::getBindingUserId, userId));
        if (CollectionUtils.isEmpty(employeeList)) {
            log.warn("userCurrent deptTreeCode:-1");
            return "";
        }
        String deptTreeCode = employeeList.get(0).getDepartmentTreeCode();
        log.info("userCurrent deptTreeCode:{}", deptTreeCode);

        return StringUtils.isNotBlank(deptTreeCode) ? deptTreeCode : "";
    }

    /**
     * 获取用户所有绑定员工的部门 TREECODE-树编码
     *
     * @return
     */
    public String userDeptCodes(Long userId) {
        if (userId == null) {
            log.warn("userCurrent deptTreeCode:-1");
        }

        List<PamirsEmployee> employeeList = new PamirsEmployee()
                .queryList(Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).eq(PamirsEmployee::getBindingUserId, userId));
        if (CollectionUtils.isEmpty(employeeList)) {
            log.warn("userCurrent deptTreeCode:-1");
        }
        String deptTreeCodes = employeeList.stream().map(PamirsEmployee::getDepartmentTreeCode).filter(Objects::nonNull).distinct().map(i -> "'" + i + "'").collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA));
        log.info("userCurrent deptTreeCode:{}", deptTreeCodes);

        return StringUtils.isNotBlank(deptTreeCodes) ? "(" + deptTreeCodes + ")" : "''";
    }

    /**
     * 获取用户所有绑定员工的部门 TREECODE-树编码
     *
     * @return
     */
    public String userDeptWithChildCodes(Long userId) {
        if (userId == null) {
            log.warn("userCurrent deptTreeCode:-1");
        }

        List<PamirsEmployee> employeeList = new PamirsEmployee()
                .queryList(Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL).eq(PamirsEmployee::getBindingUserId, userId));
        if (CollectionUtils.isEmpty(employeeList)) {
            log.warn("userCurrent deptTreeCode:-1");
        }
        List<String> departmentCodeList = employeeList.stream().map(PamirsEmployee::getDepartmentTreeCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(departmentCodeList)) {
            return "''";
        }

        List<PamirsDepartment> departmentList = departmentService.queryDepartmentChildList(Pops.<PamirsDepartment>lambdaQuery().from(PamirsDepartment.MODEL_MODEL).in(PamirsDepartment::getTreeCode, departmentCodeList));
        if (CollectionUtils.isEmpty(departmentList)) {
            log.warn("userCurrent deptTreeCode:-1");
            return "''";
        }

        String deptWithChildTreeCodes =
                departmentList.stream().map(PamirsDepartment::getTreeCode).filter(StringUtils::isNotBlank).distinct().map(i -> "'" + i + "'").collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA));
        log.info("userCurrent deptWithChildTreeCode:{}", deptWithChildTreeCodes);
        return StringUtils.isNotBlank(deptWithChildTreeCodes) ? "(" + deptWithChildTreeCodes + ")" : "''";
    }


}
