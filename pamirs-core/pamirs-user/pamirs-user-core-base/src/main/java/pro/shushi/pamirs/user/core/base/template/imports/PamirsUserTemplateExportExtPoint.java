package pro.shushi.pamirs.user.core.base.template.imports;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.extpoint.impl.DefaultExcelExportFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;
import pro.shushi.pamirs.user.core.base.pmodel.PamirsUserProxy;
import pro.shushi.pamirs.user.core.base.template.PamirsUserTemplate;
import pro.shushi.pamirs.ux.common.utils.WrapperHelper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Ext(ExcelExportTask.class)
@SuppressWarnings({"unchecked"})
public class PamirsUserTemplateExportExtPoint extends DefaultExcelExportFetchDataExtPoint {

    @Override
    @ExtPoint.Implement(expression = "context.name==\"" + PamirsUserTemplate.TEMPLATE_NAME + "\"")
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return super.fetchExportData(exportTask, context);
    }


    @Override
    protected List<?> rawQueryList(IWrapper<?> wrapper) {
        LambdaQueryWrapper<PamirsUserProxy> queryWrapper = WrapperHelper.lambda((IWrapper<PamirsUserProxy>) wrapper)
                .ne(PamirsUserProxy::getSource, UserSourceEnum.BUILD_IN.value());
        List<PamirsUserProxy> pamirsUserProxies = Models.data().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isEmpty(pamirsUserProxies)) return pamirsUserProxies;

        new PamirsUserProxy().listFieldQuery(pamirsUserProxies, PamirsUserProxy::getRoles);
        for (PamirsUserProxy pamirsUserProxy : pamirsUserProxies) {
            pamirsUserProxy.unsetInitialPassword().unsetPassword();
            List<AuthRole> roles = pamirsUserProxy.getRoles();
            if (CollectionUtils.isNotEmpty(roles)) {
                pamirsUserProxy.setRoleCode(roles.stream().map(AuthRole::getCode).collect(Collectors.joining(";")));
            }
        }
        return pamirsUserProxies;
    }


}
