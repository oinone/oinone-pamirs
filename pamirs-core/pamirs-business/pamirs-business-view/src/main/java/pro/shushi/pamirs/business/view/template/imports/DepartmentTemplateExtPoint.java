package pro.shushi.pamirs.business.view.template.imports;

import com.alibaba.excel.exception.ExcelAnalysisException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.service.PamirsDepartmentService;
import pro.shushi.pamirs.business.view.pojo.DepartmentPojo;
import pro.shushi.pamirs.business.view.template.DepartmentTemplate;
import pro.shushi.pamirs.core.common.behavior.impl.TreeCodeBehavior;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

@Component
@Ext(ExcelImportTask.class)
@Slf4j
public class DepartmentTemplateExtPoint extends AbstractExcelImportDataExtPointImpl<PamirsDepartment> {
    private static final String SEMICOLON_STRING = ";";

    @Autowired
    private PamirsDepartmentService pamirsDepartmentService;

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + DepartmentTemplate.TEMPLATE_NAME + "\"")
    public Boolean importData(ExcelImportContext importContext, PamirsDepartment data) {
        List<Object> dataBufferList = importContext.getDataBufferList();
        if (CollectionUtils.isEmpty(dataBufferList)) {
            //初始化校验文件
            initDepartment(dataBufferList);
        }
        DepartmentPojo departmentPojo = (DepartmentPojo) dataBufferList.get(0);

        //校验数据
        verifyDepartment(data, departmentPojo);
        //默认值填充
        defaultValueFiller(data, departmentPojo);

        Map<String, PamirsDepartment> codeByDepartmentMapExcel = departmentPojo.getCodeByDepartmentMapExcel();
        codeByDepartmentMapExcel.put(data.getCode(), data);

        calcExcel(data);
        return true;
    }

    private void calcExcel(PamirsDepartment pamirsDepartment) {
        LambdaQueryWrapper<PamirsDepartment> queryWrapper = Pops.<PamirsDepartment>lambdaQuery().from(PamirsDepartment.MODEL_MODEL)
                .eq(PamirsDepartment::getCode, pamirsDepartment.getCode())
                .eq(PamirsDepartment::getTreeCode, pamirsDepartment.getTreeCode());

        Long count = new PamirsDepartment().count(queryWrapper);
        if (count > 0) {
            pamirsDepartmentService.update(pamirsDepartment);
        } else {
            pamirsDepartmentService.create(pamirsDepartment);
        }
    }

    private void defaultValueFiller(PamirsDepartment data, DepartmentPojo departmentPojo) {
        Stack<PamirsDepartment> priorityStack = departmentPojo.getPriorityStack();
        data.setPriority(Instant.now().toEpochMilli());
        data.setDepartmentType(BusinessModule.DEFAULT_TYPE);
        data.setDataStatus(DataStatusEnum.ENABLED);

        if (!priorityStack.empty()) {
            PamirsDepartment pamirsDepartment = priorityStack.pop();
            if (pamirsDepartment.getCode().equals(data.getCode())) {
                priorityStack.push(data);
            }
        }
    }


    private void verifyDepartment(PamirsDepartment data, DepartmentPojo departmentPojo) {
        StringBuilder errorMessage = new StringBuilder();
        Boolean flag = Boolean.FALSE;

        Map<String, PamirsCompany> companyHashMap = departmentPojo.getCompanyHashMap();
        Map<String, PamirsDepartment> codeByDepartmentHashMap = departmentPojo.getCodeByDepartmentHashMap();
        Map<String, PamirsDepartment> codeByDepartmentMapExcel = departmentPojo.getCodeByDepartmentMapExcel();
        Stack<PamirsDepartment> priorityStack = departmentPojo.getPriorityStack();

        Map<String, PamirsDepartment> keyByDepartmentMap = new HashMap<>();
        Map<String, PamirsDepartment> keyByDepartmentExcelMap = new HashMap<>();

        if (!codeByDepartmentHashMap.isEmpty()) {
            keyByDepartmentMap = codeByDepartmentHashMap.values().stream().collect(Collectors.toMap(DepartmentTemplateExtPoint::generatorKey, pamirsDepartment -> pamirsDepartment));
        }
        if (!codeByDepartmentMapExcel.isEmpty()) {
            keyByDepartmentExcelMap = codeByDepartmentMapExcel.values().stream().collect(Collectors.toMap(DepartmentTemplateExtPoint::generatorKey, pamirsDepartment -> pamirsDepartment));
        }

        if (StringUtils.isEmpty(data.getCode())) {
            throw new ExcelAnalysisException(BusinessExpEnumerate.DEPARTMENT_CODE_NOT_EMPTY_EXCEPTION.msg());
        }
        String parentCode = data.getParentCode();
        if (StringUtils.isNotBlank(parentCode) && data.getCode().equals(parentCode)){
            throw new ExcelAnalysisException(BusinessExpEnumerate.SELF_PARENT_DEPARTMENT_EXCEPTION.msg());
        }

        String companyCode = data.getCompanyCode();

        // 同公司 同级别 部门名称 不允许相同
        if (StringUtils.isEmpty(data.getName())) {
            errorMessage.append(BusinessExpEnumerate.DEPARTMENT_NAME_NOT_EMPTY_EXCEPTION.msg()).append(" ");
        } else if (StringUtils.isNotBlank(companyCode)) {
            String key = generatorKey(data);
            if (keyByDepartmentMap.containsKey(key) || keyByDepartmentExcelMap.containsKey(key)) {
                PamirsDepartment pamirsDepartmentExcel = keyByDepartmentExcelMap.get(key);
                PamirsDepartment pamirsDepartmentMetaData = keyByDepartmentMap.get(key);
                if (StringUtils.isNotEmpty(data.getCode())) {
                    if (pamirsDepartmentExcel != null && !(pamirsDepartmentExcel.getCode().equals(data.getCode()))) {
                        errorMessage.append(BusinessExpEnumerate.DUPLICATE_DEPARTMENT_NAME_EXCEPTION.msg()).append(" ");
                    }
                    if (pamirsDepartmentMetaData != null && !(pamirsDepartmentMetaData.getCode().equals(data.getCode()))) {
                        errorMessage.append(BusinessExpEnumerate.DUPLICATE_DEPARTMENT_NAME_EXCEPTION.msg()).append(" ");
                    }
                }
            }
        }

        if (StringUtils.isBlank(companyCode)) {
            errorMessage.append(BusinessExpEnumerate.COMPANY_CODE_EMPTY.msg()).append(" ");
        } else if (!(companyHashMap.containsKey(companyCode))) {
            errorMessage.append(BusinessExpEnumerate.COMPANY_NOT_CREATED_EXCEPTION.msg()).append(" ");
        }

        if (data.getCompany() == null || StringUtils.isBlank(data.getCompany().getName())) {
            errorMessage.append(BusinessExpEnumerate.COMPANY_NAME.msg()).append(" ");
        }


        if (StringUtils.isNotBlank(data.getParentCode()) && StringUtils.isNotBlank(companyCode)) {
            if (codeByDepartmentHashMap.containsKey(data.getParentCode())
                    && codeByDepartmentHashMap.get(data.getParentCode()).getCompanyCode().equals(companyCode)) {
                PamirsDepartment pamirsDepartmentDb = codeByDepartmentHashMap.get(data.getParentCode());
                if (StringUtils.isNotBlank(pamirsDepartmentDb.getTreeCode())) {
                    data.setTreeCode(TreeCodeBehavior.concat(pamirsDepartmentDb.getTreeCode(), data.getCode()));
                } else {
                    data.setTreeCode(TreeCodeBehavior.concat(pamirsDepartmentDb.getCode(), data.getCode()));
                }
            } else if (codeByDepartmentMapExcel.containsKey(data.getParentCode())
                    && codeByDepartmentMapExcel.get(data.getParentCode()).getCompanyCode().equals(companyCode)) {

                PamirsDepartment pamirsDepartment = codeByDepartmentMapExcel.get(data.getParentCode());
                if (StringUtils.isNotBlank(pamirsDepartment.getTreeCode())) {
                    data.setTreeCode(TreeCodeBehavior.concat(pamirsDepartment.getTreeCode(), data.getCode()));
                } else {
                    data.setTreeCode(TreeCodeBehavior.concat(pamirsDepartment.getCode(), data.getCode()));
                }
                flag = Boolean.TRUE;
            } else {
                errorMessage.append(BusinessExpEnumerate.PARENT_DEPARTMENT_NOT_FOUND_EXCEPTION.msg()).append(" ");
            }
        } else {
            data.setTreeCode(data.getCode());
            data.setParentCode(null);
        }

        if (StringUtils.isNotEmpty(errorMessage)) {
            throw new ExcelAnalysisException(errorMessage.toString());
        }
        if (flag) {
            priorityStack.push(data);
        }
    }


    private void initDepartment(List<Object> dataBufferList) {
        List<PamirsCompany> pamirsCompanies = new PamirsCompany().queryList();
        List<PamirsDepartment> pamirsDepartments = new PamirsDepartment().queryList();

        Map<String, PamirsCompany> companyHashMap = new HashMap<>();
        Map<String, PamirsDepartment> departmentHashMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(pamirsCompanies)) {
            companyHashMap = pamirsCompanies.stream().collect(Collectors.toMap(PamirsCompany::getCode, pamirsCompany -> pamirsCompany));
        }
        if (CollectionUtils.isNotEmpty(pamirsDepartments)) {
            departmentHashMap = pamirsDepartments.stream().collect(Collectors.toMap(PamirsDepartment::getCode, pamirsDepartment -> pamirsDepartment));
        }

        DepartmentPojo departmentPojo = DepartmentPojo.of(companyHashMap, departmentHashMap);
        dataBufferList.add(0, departmentPojo);
    }

    public static String generatorKey(PamirsDepartment department) {
        String key = department.getCompanyCode() + SEMICOLON_STRING + department.getName();
        if (StringUtils.isNotBlank(department.getParentCode())) {
            key += SEMICOLON_STRING + department.getParentCode();
        }
        return key;
    }
}
