package pro.shushi.pamirs.auth.core.template;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRoleType;
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
 * role type excel import/export template
 *
 * @author Adamancy Zhang at 21:21 on 2024-03-20
 */
@Component
public class AuthRoleTypeTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "authRoleTypeTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(AuthRoleType.MODEL_MODEL, TEMPLATE_NAME)
                .setDisplayName("角色类型");

        AuthRoleTypeTemplate.createRoleTypeSheet(builder);

        return Collections.singletonList(builder.build());
    }

    public static void createRoleTypeSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("角色类型")
                .createBlock(AuthRoleType.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:C2")
                .setPresetNumber(10)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("description").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("角色类型编码").and()
                .createCell().setValue("角色类型名称").and()
                .createCell().setValue("角色类型描述");
    }

}
