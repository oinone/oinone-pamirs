package pro.shushi.pamirs.business.view.template;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.pmodel.PamirsEmployeeProxy;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;

import java.util.Collections;
import java.util.List;

@Component
public class EmployeeTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "employeeTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(PamirsEmployee.MODEL_MODEL, TEMPLATE_NAME).setDisplayName("员工-导入导出");

        EmployeeTemplate.createEmployeeSheet(builder);

        return Collections.singletonList(builder.build());
    }

    private static void createEmployeeSheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName("员工")
                .createBlock(PamirsEmployeeProxy.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:K3")
                .createMergeRange("A1:K1")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("companyCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("company.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("login").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("departmentCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("department.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("departmentCodeList").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("departmentNameList").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("initialPassword").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("roleCodes").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(2600))
                .createCell().setValue("1.红色为必填字段。\n" +
                        "2.员工编码相同新数据直接覆盖旧数据。\n" +
                        "3.若存在多个“所属部门”、多个“角色编码”用英文 “;” 分割，请确保填写的每个被绑定的编码都是系统中已存在的有效编码。\n" +
                        "4.初始密码需为8至32位；如未填写，则默认为“Abcd@1234”。\n" +
                        "5.登录账号和初始密码的作用是用于创建新用户或绑定现有用户。\n" +
                        "6.同一公司下的员工不允许绑定相同账户。")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 179, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).build()))
                        .add(new RichTextFormat(0, 10, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("员工编码")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("员工名称")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("所属公司编码")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 6, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("所属公司名称")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 6, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("登录账号")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("所属主部门编码").and()
                .createCell().setValue("所属主部门名称").and()
                .createCell().setValue("所属部门编码").and()
                .createCell().setValue("所属部门名称").and()
                .createCell().setValue("初始密码").and()
                .createCell().setValue("角色编码");
    }
}
