package pro.shushi.pamirs.auth.core.template;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthModelPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleModelPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelExportStrategyEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;

import java.util.Collections;
import java.util.List;

/**
 * role and all permissions excel import/export template
 *
 * @author Adamancy Zhang at 11:06 on 2024-03-19
 */
@Component
public class AuthRolePermissionTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "authRolePermissionTemplate";

    private static final int PATH_WIDTH = 20000;

    private static final int FILTER_WIDTH = 10000;

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(AuthRole.MODEL_MODEL, TEMPLATE_NAME).setDisplayName("角色及全部权限")
                .setExportStrategy(ExcelExportStrategyEnum.BLOCK);

        AuthRoleTemplate.createRoleSheet(builder);

        AuthRolePermissionTemplate.createResourcePermissionSheet(builder);

        AuthRolePermissionTemplate.createRoleResourcePermissionSheet(builder);

        AuthRolePermissionTemplate.createModelPermissionSheet(builder);

        AuthRolePermissionTemplate.createRoleModelPermissionSheet(builder);

        AuthRolePermissionTemplate.createFieldPermissionSheet(builder);

        AuthRolePermissionTemplate.createRoleFieldPermissionSheet(builder);

        AuthRolePermissionTemplate.createRowPermissionSheet(builder);

        AuthRolePermissionTemplate.createRoleRowPermissionSheet(builder);

        return Collections.singletonList(builder.build());
    }

    public static void createResourcePermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("资源权限项")
                .createBlock(AuthResourcePermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:I2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("type").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setResourceTypeFormat).and()
                .createCell().setField("subtype").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setResourceSubtypeFormat).and()
                .createCell().setField("module").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("model").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("name").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("path").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(PATH_WIDTH)).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("active").apply(AuthTemplateHelper::setActiveFormat).and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("资源权限项编码").and()
                .createCell().setValue("资源权限项类型").and()
                .createCell().setValue("资源权限项子类型").and()
                .createCell().setValue("资源模块").and()
                .createCell().setValue("资源模型").and()
                .createCell().setValue("资源名称").and()
                .createCell().setValue("资源路径").and()
                .createCell().setValue("权限项来源").and()
                .createCell().setValue("是否激活");
    }

    public static void createRoleResourcePermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("资源权限授权")
                .createBlock(AuthRoleResourcePermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:F2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("role.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permissionType").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setResourceTypeFormat).and()
                .createCell().setField("permissionSubtype").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setResourceSubtypeFormat).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("authorizedValue").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("角色编码").and()
                .createCell().setValue("资源权限项编码").and()
                .createCell().setValue("资源权限项类型").and()
                .createCell().setValue("资源权限项子类型").and()
                .createCell().setValue("授权来源").and()
                .createCell().setValue("权限值");
    }

    private static void createModelPermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("模型权限项")
                .createBlock(AuthModelPermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:F2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("model").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("inherit").apply(AuthTemplateHelper::setInheritFormat).and()
                .createCell().setField("path").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(PATH_WIDTH)).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("active").apply(AuthTemplateHelper::setActiveFormat).and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("模型权限项编码").and()
                .createCell().setValue("模型编码").and()
                .createCell().setValue("子模型有效").and()
                .createCell().setValue("资源路径").and()
                .createCell().setValue("权限项来源").and()
                .createCell().setValue("是否激活");
    }

    private static void createRoleModelPermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("模型权限授权")
                .createBlock(AuthRoleModelPermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:E2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("role.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.model").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("authorizedValue").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("角色编码").and()
                .createCell().setValue("模型权限项编码").and()
                .createCell().setValue("模型编码").and()
                .createCell().setValue("授权来源").and()
                .createCell().setValue("权限值");
    }

    private static void createFieldPermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("字段权限项")
                .createBlock(AuthFieldPermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:G2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("model").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("field").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("inherit").apply(AuthTemplateHelper::setInheritFormat).and()
                .createCell().setField("path").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(PATH_WIDTH)).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("active").apply(AuthTemplateHelper::setActiveFormat).and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("字段权限项编码").and()
                .createCell().setValue("模型编码").and()
                .createCell().setValue("字段编码").and()
                .createCell().setValue("子模型有效").and()
                .createCell().setValue("资源路径").and()
                .createCell().setValue("权限项来源").and()
                .createCell().setValue("是否激活");
    }

    private static void createRoleFieldPermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("字段权限授权")
                .createBlock(AuthRoleFieldPermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:F2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("role.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.model").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.field").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("authorizedValue").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("角色编码").and()
                .createCell().setValue("字段权限项编码").and()
                .createCell().setValue("模型编码").and()
                .createCell().setValue("字段编码").and()
                .createCell().setValue("授权来源").and()
                .createCell().setValue("权限值");
    }

    private static void createRowPermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("数据权限项")
                .createBlock(AuthRowPermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:K2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("displayName").and()
                .createCell().setField("description").and()
                .createCell().setField("model").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("filter").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(FILTER_WIDTH)).and()
                .createCell().setField("domainExpDisplayName").and()
                .createCell().setField("domainExpJson").and()
                .createCell().setField("inherit").apply(AuthTemplateHelper::setInheritFormat).and()
                .createCell().setField("path").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(PATH_WIDTH)).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("active").apply(AuthTemplateHelper::setActiveFormat).and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("数据权限项编码").and()
                .createCell().setValue("名称").and()
                .createCell().setValue("描述").and()
                .createCell().setValue("模型编码").and()
                .createCell().setValue("过滤条件").and()
                .createCell().setValue("表达式显示名称").and()
                .createCell().setValue("表达式结构JSON").and()
                .createCell().setValue("子模型有效").and()
                .createCell().setValue("资源路径").and()
                .createCell().setValue("权限项来源").and()
                .createCell().setValue("是否激活");
    }

    private static void createRoleRowPermissionSheet(WorkbookDefinitionBuilder builder) {
        builder.createSheet().setName("行权限授权")
                .createBlock(AuthRoleRowPermission.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:F2")
                .setDomain(AuthTemplateHelper.getSourceFilter())
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("role.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.code").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.model").setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("permission.filter").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(FILTER_WIDTH)).and()
                .createCell().setField("source").setAutoSizeColumn(Boolean.TRUE).apply(AuthTemplateHelper::setSourceFormat).and()
                .createCell().setField("authorizedValue").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("角色编码").and()
                .createCell().setValue("数据权限项编码").and()
                .createCell().setValue("模型编码").and()
                .createCell().setValue("过滤条件").and()
                .createCell().setValue("授权来源").and()
                .createCell().setValue("权限值");
    }
}
