package pro.shushi.pamirs.business.view.template;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
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
public class DepartmentTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "pamirsDepartmentTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(PamirsDepartment.MODEL_MODEL, TEMPLATE_NAME).setDisplayName("部门-导入导出");
        DepartmentTemplate.createDepartmentSheet(builder);
        return Collections.singletonList(builder.build());
    }

    private static void createDepartmentSheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName("部门")
                .createBlock(PamirsDepartment.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:G3")
                .createMergeRange("A1:G1")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).setAutoSizeColumn(Boolean.TRUE).and()
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("companyCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("company.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("parentCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("parent.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("description").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(1200))
                .createCell().setValue(
                        "1.红色为必填字段\n" +
                                "2.部门编码相同新数据直接覆盖旧数据\n" +
                                "3.在同一公司内，同一级别的“部门名称”不允许重复 \n" +
                                "4.请确保填写的每个被绑定的编码都是系统中已存在的有效编码。")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 86, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).build()))
                        .add(new RichTextFormat(0, 9, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("部门编码")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("部门名称")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("所属公司编码")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 6, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("所属公司名称").and()
                .createCell().setValue("上级部门编码").and()
                .createCell().setValue("上级部门名称").and()
                .createCell().setValue("备注");
    }
}
