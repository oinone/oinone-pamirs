package pro.shushi.pamirs.business.view.template.imports;

import com.alibaba.excel.exception.ExcelAnalysisException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BindingModeEnum;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.pmodel.PamirsEmployeeProxy;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.business.view.pojo.EmployeePojo;
import pro.shushi.pamirs.business.view.template.EmployeeTemplate;
import pro.shushi.pamirs.core.common.check.UserInfoChecker;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Ext(ExcelImportTask.class)
@Slf4j
public class EmployeeTemplateImportExtPoint extends AbstractExcelImportDataExtPointImpl<PamirsEmployeeProxy> {
    private static final String SEMICOLON_STRING = ";";
    private static final String INITIAL_PASSWORD = "Abcd@1234";

    @Autowired
    private PamirsEmployeeService pamirsEmployeeService;


    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + EmployeeTemplate.TEMPLATE_NAME + "\"")
    public Boolean importData(ExcelImportContext importContext, PamirsEmployeeProxy data) {
        List<Object> dataBufferList = importContext.getDataBufferList();
        if (CollectionUtils.isEmpty(dataBufferList)) {
            initEmployee(dataBufferList);
        }

        EmployeePojo employeePojo = (EmployeePojo) dataBufferList.get(0);
        //校验数据
        dataValidator(data, employeePojo);
        //填充默认值
        defaultValueFiller(data, employeePojo);

        Map<String, PamirsEmployee> pamirsEmployees = employeePojo.getPamirsEmployeeMap();
        pamirsEmployees.put(data.getCode(), data);
        calcExcel(data);
        return true;
    }

    private void calcExcel(PamirsEmployeeProxy pamirsEmployeeProxy) {
        LambdaQueryWrapper<PamirsEmployee> qw = Pops.<PamirsEmployee>lambdaQuery().from(PamirsEmployee.MODEL_MODEL)
                .eq(PamirsEmployee::getCode, pamirsEmployeeProxy.getCode());

        Long count = new PamirsEmployeeProxy().count(qw);
        if (count > 0) {
            PamirsEmployee pamirsEmployee = new PamirsEmployee().queryOneByWrapper(qw);
            if (BindingModeEnum.BINDING_EXISTING.equals(pamirsEmployeeProxy.getBindingMode())) {
                pamirsEmployeeProxy.unsetInitialPassword();
            }
            pamirsEmployeeProxy.setId(pamirsEmployee.getId());
            pamirsEmployeeService.update(pamirsEmployeeProxy);
        } else {
            if (BindingModeEnum.CREATE_BINDING.equals(pamirsEmployeeProxy.getBindingMode())) {
                pamirsEmployeeService.createEmployeeAndUser(pamirsEmployeeProxy);
            } else if (BindingModeEnum.BINDING_EXISTING.equals(pamirsEmployeeProxy.getBindingMode())) {
                pamirsEmployeeProxy.unsetInitialPassword();
                pamirsEmployeeService.create(pamirsEmployeeProxy);
            }
        }
    }

    private void defaultValueFiller(PamirsEmployeeProxy data, EmployeePojo employeePojo) {
        Map<String, PamirsCompany> pamirsCompanyMap = employeePojo.getCompanyHashMap();
        Map<String, PamirsDepartment> departmentHashMap = employeePojo.getDepartmentHashMap();
        String companyCode = data.getCompanyCode();
        String departmentCode = data.getDepartmentCode();

        LambdaQueryWrapper<PamirsUser> userLambdaQueryWrapper = Pops.<PamirsUser>lambdaQuery().from(PamirsUser.MODEL_MODEL)
                .eq(PamirsUser::getLogin, data.getLogin());
        Long count = new PamirsUser().count(userLambdaQueryWrapper);
        if (count < 1) {
            data.setBindingMode(BindingModeEnum.CREATE_BINDING);
        } else {
            PamirsUser pamirsUser = new PamirsUser().queryOneByWrapper(userLambdaQueryWrapper);
            Long id = pamirsUser.getId();
            LambdaQueryWrapper<PamirsEmployee> employeeQueryWrapper = Pops.<PamirsEmployee>lambdaQuery()
                    .from(PamirsEmployee.MODEL_MODEL)
                    .eq(PamirsEmployee::getBindingUserId, id).eq(PamirsEmployee::getCompanyCode, companyCode);
            Long employeeCount = new PamirsEmployee().count(employeeQueryWrapper);
            if (employeeCount > 0) {
                PamirsEmployee pamirsEmployee = new PamirsEmployee().queryOneByWrapper(employeeQueryWrapper);
                if (!pamirsEmployee.getCode().equals(data.getCode())) {
                    throw new ExcelAnalysisException(BusinessExpEnumerate.DUPLICATE_ACCOUNT_BINDING_EXCEPTION.msg());
                }
            }


            data.setBindingMode(BindingModeEnum.BINDING_EXISTING);
            data.setBindingUserId(pamirsUser.getId());
        }

        data.setDataStatus(DataStatusEnum.ENABLED);
        if (companyCode != null) {
            data.setCompany(pamirsCompanyMap.get(companyCode));
        }
        if (departmentCode != null) {
            data.setDepartment(departmentHashMap.get(departmentCode));
        }
        if (StringUtils.isEmpty(data.getInitialPassword()))
            data.setInitialPassword(INITIAL_PASSWORD);
    }

    private void initEmployee(List<Object> dataBufferList) {
        List<PamirsCompany> pamirsCompanies = new PamirsCompany().queryList();
        List<PamirsDepartment> pamirsDepartments = new PamirsDepartment().queryList();
        List<AuthRole> authRoles = new AuthRole().queryList();

        Map<String, PamirsCompany> companyHashMap = new HashMap<>();
        Map<String, PamirsDepartment> departmentHashMap = new HashMap<>();
        Map<String, AuthRole> authRoleHashMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(pamirsCompanies)) {
            companyHashMap = pamirsCompanies.stream().collect(Collectors.toMap(PamirsCompany::getCode, pamirsCompany -> pamirsCompany));
        }
        if (CollectionUtils.isNotEmpty(pamirsDepartments)) {
            departmentHashMap = pamirsDepartments.stream().collect(Collectors.toMap(PamirsDepartment::getCode, pamirsDepartment -> pamirsDepartment));
        }
        if (CollectionUtils.isNotEmpty(authRoles)) {
            authRoleHashMap = authRoles.stream().collect(Collectors.toMap(AuthRole::getCode, role -> role));
        }

        EmployeePojo employeePojo = EmployeePojo.of(companyHashMap, departmentHashMap, authRoleHashMap);
        dataBufferList.add(0, employeePojo);
    }


    private void dataValidator(PamirsEmployeeProxy data, EmployeePojo employeePojo) {
        StringBuilder errorMessage = new StringBuilder();
        Map<String, PamirsCompany> pamirsCompanyMap = employeePojo.getCompanyHashMap();
        Map<String, PamirsDepartment> departmentHashMap = employeePojo.getDepartmentHashMap();
        Map<String, AuthRole> authRoleHashMap = employeePojo.getAuthRoleHashMap();

        if (StringUtils.isEmpty(data.getCode())) {
            errorMessage.append(BusinessExpEnumerate.EMPLOYEE_CODE.msg()).append(" ");
        }

        if (StringUtils.isEmpty(data.getName())) {
            errorMessage.append(BusinessExpEnumerate.EMPLOYEE_NAME.msg()).append(" ");
        } else if (Boolean.FALSE.equals(UserInfoChecker.checkRealname(data.getName()))) {
            errorMessage.append(BusinessExpEnumerate.EMPLOYEE_NAME_ERROR.msg()).append(" ");
        }

        if (pamirsCompanyMap.isEmpty()) {
            errorMessage.append(BusinessExpEnumerate.COMPANY_CODE.msg()).append(" ");
        } else if (data.getCompanyCode() != null && !(pamirsCompanyMap.containsKey(data.getCompanyCode()))) {
            errorMessage.append(BusinessExpEnumerate.COMPANY_NOT_CREATED_EXCEPTION.msg()).append(" ");
        }

        if (StringUtils.isEmpty(data.getLogin())) {
            errorMessage.append(BusinessExpEnumerate.LOGIN_ACCOUNT_NOT_EMPTY_EXCEPTION.msg()).append(" ");
        } else if (Boolean.FALSE.equals(UserInfoChecker.checkLogin(data.getLogin()))) {
            errorMessage.append(BusinessExpEnumerate.USER_PARAM_LOGIN_ERROR.msg()).append(" ");
        }

        if (data.getInitialPassword() != null && Boolean.FALSE.equals(UserInfoChecker.checkPassword(data.getInitialPassword())))
            errorMessage.append(UserExpEnumerate.USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR.msg()).append(" ");

        if (data.getDepartmentCode() != null) {
            if (departmentHashMap.isEmpty()) {
                errorMessage.append(BusinessExpEnumerate.MAIN_DEPARTMENT_NOT_FOUND_EXCEPTION.msg()).append(" ");
            } else {
                PamirsDepartment pamirsDepartment = departmentHashMap.get(data.getDepartmentCode());
                if (pamirsDepartment == null) {
                    errorMessage.append(BusinessExpEnumerate.MAIN_DEPARTMENT_NOT_FOUND_EXCEPTION.msg()).append(" ");
                } else {
                    if (data.getCompanyCode() != null && !(pamirsDepartment.getCompanyCode().equals(data.getCompanyCode()))) {
                        errorMessage.append(BusinessExpEnumerate.MAIN_DEPARTMENT_NOT_FOUND_EXCEPTION.msg()).append(" ");
                    }
                }
            }
        }

        String departmentCodeList = data.getDepartmentCodeList();
        if (StringUtils.isNotEmpty(departmentCodeList)) {
            if (departmentHashMap.isEmpty())
                errorMessage.append(BusinessExpEnumerate.DEPARTMENT_CODE_NOT_FOUND_EXCEPTION.msg()).append(" ");
            String[] departmentCodes = departmentCodeList.split(SEMICOLON_STRING);
            StringBuilder errorCode = new StringBuilder();
            List<PamirsDepartment> departmentList = new ArrayList<>();
            for (String code : departmentCodes) {
                PamirsDepartment pamirsDepartment = departmentHashMap.get(code);
                if (pamirsDepartment == null || (data.getCompanyCode() != null && !(pamirsDepartment.getCompanyCode().equals(data.getCompanyCode())))) {
                    errorCode.append(code).append(" ");
                } else {
                    departmentList.add(pamirsDepartment);
                }
            }
            if (StringUtils.isNotEmpty(errorCode)) {
                errorMessage.append(PStringUtils.parse1(BusinessExpEnumerate.DEPARTMENT_NOT_FOUND_EXCEPTION.msg(), errorCode)).append(" ");
            }
            data.setDepartmentList(departmentList);
        }


        String roleCode = data.getRoleCodes();
        if (StringUtils.isNotEmpty(roleCode)) {
            if (authRoleHashMap.isEmpty()) errorMessage.append(UserExpEnumerate.INVALID_ROLE_CODE.msg()).append(" ");
            String[] roleCodes = roleCode.split(SEMICOLON_STRING);
            StringBuilder errorCode = new StringBuilder();
            List<AuthRole> authRoles = new ArrayList<>();
            for (String code : roleCodes) {
                AuthRole authRole = authRoleHashMap.get(code);
                if (authRole == null) {
                    errorCode.append(code).append(" ");
                } else {
                    authRoles.add(authRole);
                }
            }
            if (StringUtils.isNotEmpty(errorCode)) {
                errorMessage.append(PStringUtils.parse1(UserExpEnumerate.INVALID_ROLE_CODE_EXCEPTION.msg(), errorCode)).append(" ");

            }
            data.setRoles(authRoles);
        }

        if (StringUtils.isNotEmpty(errorMessage)) {
            throw new ExcelAnalysisException(errorMessage.toString());
        }
    }
}
