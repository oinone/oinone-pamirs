package pro.shushi.pamirs.business.view.template.imports;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.pmodel.PamirsEmployeeProxy;
import pro.shushi.pamirs.business.view.template.EmployeeTemplate;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.extpoint.impl.DefaultExcelExportFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wuxin
 * @Date 2024/6/28
 * @since 1.0
 */
@Slf4j
@Component
@Ext(ExcelExportTask.class)
@SuppressWarnings({"unchecked"})
public class EmployeeTemplateExportExtPoint extends DefaultExcelExportFetchDataExtPoint {

    @Override
    @ExtPoint.Implement(expression = "context.name==\"" + EmployeeTemplate.TEMPLATE_NAME + "\"")
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return super.fetchExportData(exportTask, context);
    }

    @Override
    protected List<?> rawQueryList(IWrapper<?> wrapper) {
        List<PamirsEmployeeProxy> pamirsEmployeeProxies = (List<PamirsEmployeeProxy>) Models.data().queryListByWrapper(wrapper);
        if (CollectionUtils.isNotEmpty(pamirsEmployeeProxies)) {
            new PamirsEmployeeProxy().listFieldQuery(pamirsEmployeeProxies, PamirsEmployee::getDepartmentList);
            new PamirsEmployeeProxy().listFieldQuery(pamirsEmployeeProxies, PamirsEmployeeProxy::getRoles);
            new PamirsEmployeeProxy().listFieldQuery(pamirsEmployeeProxies, PamirsEmployeeProxy::getDefaultBindingUser);
            for (PamirsEmployeeProxy pamirsEmployeeProxy : pamirsEmployeeProxies) {
                List<PamirsDepartment> departmentList = pamirsEmployeeProxy.getDepartmentList();
                if (CollectionUtils.isNotEmpty(departmentList)) {
                    if (CollectionUtils.isNotEmpty(departmentList)) {
                        pamirsEmployeeProxy.setDepartmentCodeList(departmentList.stream().map(PamirsDepartment::getCode).collect(Collectors.joining(";")));
                    }
                    if (CollectionUtils.isNotEmpty(departmentList)) {
                        pamirsEmployeeProxy.setDepartmentNameList(departmentList.stream().map(PamirsDepartment::getName).collect(Collectors.joining(";")));
                    }
                }

                List<AuthRole> roles = pamirsEmployeeProxy.getRoles();
                if (CollectionUtils.isNotEmpty(roles)) {
                    pamirsEmployeeProxy.setRoleCodes(roles.stream().map(AuthRole::getCode).collect(Collectors.joining(";")));
                }
                PamirsUser user = pamirsEmployeeProxy.getDefaultBindingUser();
                if (user != null) {
                    pamirsEmployeeProxy.setLogin(user.getLogin());
                }
            }
        }
        return pamirsEmployeeProxies;
    }

}
