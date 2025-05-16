package pro.shushi.pamirs.auth.core.template.imports;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.core.template.AuthRoleTemplate;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

/**
 * {@link AuthRoleTemplate} Import ExtPoint
 *
 * @author Adamancy Zhang at 12:24 on 2024-03-19
 */
@Component
@Ext(ExcelImportTask.class)
public class AuthRoleImportExtPoint extends AbstractExcelImportDataExtPointImpl<AuthRole> {

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + AuthRoleTemplate.TEMPLATE_NAME + "\"")
    public Boolean importData(ExcelImportContext importContext, AuthRole data) {
        return super.importData(importContext, data);
    }
}
