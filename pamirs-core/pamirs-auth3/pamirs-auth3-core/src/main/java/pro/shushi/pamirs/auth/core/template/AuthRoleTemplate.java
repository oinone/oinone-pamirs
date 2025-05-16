package pro.shushi.pamirs.auth.core.template;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;

import java.util.Collections;
import java.util.List;

/**
 * role excel import/export template
 *
 * @author Adamancy Zhang at 10:50 on 2024-03-19
 */
@Component
public class AuthRoleTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "authRoleTemplate";

    private static final int NAME_WIDTH = 6000;

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(AuthRole.MODEL_MODEL, TEMPLATE_NAME)
                .setDisplayName("角色");

        AuthRoleTemplate.createRoleSheet(builder);

        return Collections.singletonList(builder.build());
    }

    public static void createRoleSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("角色")
                .createBlock(AuthRole.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:F2")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(NAME_WIDTH)).and()
                .createCell().setField("roleType.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("roleType.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(NAME_WIDTH)).and()
                .createCell().setField("active").apply(AuthTemplateHelper::setActiveFormat).and()
                .createCell().setField("description").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("角色编码").and()
                .createCell().setValue("角色名称").and()
                .createCell().setValue("角色类型编码").and()
                .createCell().setValue("角色类型").and()
                .createCell().setValue("是否激活").and()
                .createCell().setValue("角色描述");
    }
}
