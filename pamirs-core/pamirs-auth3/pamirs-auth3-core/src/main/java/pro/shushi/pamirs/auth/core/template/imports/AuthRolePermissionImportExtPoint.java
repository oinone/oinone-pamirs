package pro.shushi.pamirs.auth.core.template.imports;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.core.template.AuthRolePermissionTemplate;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

/**
 * {@link AuthRolePermissionTemplate} Import ExtPoint
 *
 * @author Adamancy Zhang at 12:23 on 2024-03-19
 */
@Component
@Ext(ExcelImportTask.class)
public class AuthRolePermissionImportExtPoint extends AbstractExcelImportDataExtPointImpl<Object> {

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + AuthRolePermissionTemplate.TEMPLATE_NAME + "\" && importContext.currentSheetNumber == 0")
    public Boolean importData(ExcelImportContext importContext, Object data) {
        return super.importData(importContext, data);
    }
}
