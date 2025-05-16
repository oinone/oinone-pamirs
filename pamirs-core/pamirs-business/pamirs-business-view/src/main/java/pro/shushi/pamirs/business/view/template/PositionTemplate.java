package pro.shushi.pamirs.business.view.template;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
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
public class PositionTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "pamirsPositionTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(PamirsPosition.MODEL_MODEL, TEMPLATE_NAME).setDisplayName("岗位-导入导出");

        PositionTemplate.createPositionSheet(builder);

        return Collections.singletonList(builder.build());
    }

    private static void createPositionSheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName("岗位")
                .createBlock(PamirsPosition.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:H3")
                .createMergeRange("A1:H1")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("parentCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("parent.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()

                .createCell().setField("companyCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("company.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()

                .createCell().setField("departmentCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("department.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()

                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(800))
                .createCell().setValue("1.红色为必填字段。\n" +
                        "2.岗位编码相同新数据直接覆盖旧数据。\n" +
                        "3.请确保填写的每个被绑定的编码都是系统中已存在的有效编码。")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 61, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).build()))
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
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("岗位编码")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("岗位名称")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("直属岗位编码").and()
                .createCell().setValue("直属岗位名称").and()
                .createCell().setValue("所属公司编码").and()
                .createCell().setValue("所属公司名称").and()
                .createCell().setValue("所属部门编码").and()
                .createCell().setValue("所属部门名称");
    }
}
