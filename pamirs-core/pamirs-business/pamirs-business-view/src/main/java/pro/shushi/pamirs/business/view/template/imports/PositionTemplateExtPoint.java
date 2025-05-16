package pro.shushi.pamirs.business.view.template.imports;

import com.alibaba.excel.exception.ExcelAnalysisException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.business.view.pojo.PositionPojo;
import pro.shushi.pamirs.business.view.template.PositionTemplate;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ranjingnian
 */
@Component
@Ext(ExcelImportTask.class)
@Slf4j
public class PositionTemplateExtPoint extends AbstractExcelImportDataExtPointImpl<PamirsPosition> {

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + PositionTemplate.TEMPLATE_NAME + "\"")
    public Boolean importData(ExcelImportContext importContext, PamirsPosition data) {
        List<Object> dataBufferList = importContext.getDataBufferList();
        if (CollectionUtils.isEmpty(dataBufferList)) {
            //初始化校验文件
            initPosition(dataBufferList);
        }
        PositionPojo positionPojo = (PositionPojo) dataBufferList.get(0);

        //校验数据
        verifyPosition(data, positionPojo);
        data.setDataStatus(DataStatusEnum.ENABLED);
        data.createOrUpdate();
        return true;
    }


    private void verifyPosition(PamirsPosition data, PositionPojo positionPojo) {
        StringBuilder errorMessage = new StringBuilder();
        Map<String, PamirsCompany> companyHashMap = positionPojo.getCompanyHashMap();
        Map<String, PamirsDepartment> departmentHashMap = positionPojo.getDepartmentHashMap();
        Map<String, PamirsPosition> pamirsPositionMap = positionPojo.getPamirsPositionMap();

        if (StringUtils.isEmpty(data.getCode())) {
            throw new ExcelAnalysisException(BusinessExpEnumerate.POSITION_CODE_NOT_EMPTY_EXCEPTION.msg());
        }
        String parentCode = data.getParentCode();
        if (StringUtils.isNotBlank(parentCode) && parentCode.equals(data.getCode())){
            throw new ExcelAnalysisException(BusinessExpEnumerate.SELF_PARENT_POSITION_EXCEPTION.msg());
        }
        if (StringUtils.isEmpty(data.getName())) {
            errorMessage.append(BusinessExpEnumerate.POSITION_NAME_NOT_EMPTY_EXCEPTION.msg()).append(" ");
        }

        if (StringUtils.isNotBlank(data.getParentCode()) && !(pamirsPositionMap.containsKey(data.getParentCode()))) {
            errorMessage.append(BusinessExpEnumerate.DIRECT_POSITION_NOT_FOUND_EXCEPTION.msg()).append(" ");
        }

        if (StringUtils.isNotBlank(data.getCompanyCode()) && !(companyHashMap.containsKey(data.getCompanyCode()))) {
            errorMessage.append(BusinessExpEnumerate.COMPANY_NOT_CREATED_EXCEPTION.msg()).append(" ");
        }

        if (StringUtils.isNotBlank(data.getDepartmentCode()) && !(departmentHashMap.containsKey(data.getDepartmentCode()))) {
            errorMessage.append(BusinessExpEnumerate.DEPARTMENT_CODE_NOT_FOUND_EXCEPTION.msg()).append(" ");
        }
        if (StringUtils.isNotEmpty(errorMessage)) {
            throw new ExcelAnalysisException(errorMessage.toString());
        }
    }

    private void initPosition(List<Object> dataBufferList) {
        List<PamirsCompany> pamirsCompanies = new PamirsCompany().queryList();
        List<PamirsDepartment> pamirsDepartments = new PamirsDepartment().queryList();
        List<PamirsPosition> pamirsPositions = new PamirsPosition().queryList();

        Map<String, PamirsCompany> companyHashMap = new HashMap<>();
        Map<String, PamirsDepartment> departmentHashMap = new HashMap<>();
        Map<String, PamirsPosition> pamirsPositionMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(pamirsCompanies)) {
            companyHashMap = pamirsCompanies.stream().collect(Collectors.toMap(PamirsCompany::getCode, pamirsCompany -> pamirsCompany));
        }
        if (CollectionUtils.isNotEmpty(pamirsDepartments)) {
            departmentHashMap = pamirsDepartments.stream().collect(Collectors.toMap(PamirsDepartment::getCode, pamirsDepartment -> pamirsDepartment));
        }
        if (CollectionUtils.isNotEmpty(pamirsPositions)) {
            pamirsPositionMap = pamirsPositions.stream().collect(Collectors.toMap(PamirsPosition::getCode, pamirsPosition -> pamirsPosition));
        }
        PositionPojo positionPojo = PositionPojo.of(companyHashMap, departmentHashMap, pamirsPositionMap);
        dataBufferList.add(0, positionPojo);
    }
}
