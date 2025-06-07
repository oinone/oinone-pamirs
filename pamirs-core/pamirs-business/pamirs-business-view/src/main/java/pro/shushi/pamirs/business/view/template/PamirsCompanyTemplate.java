package pro.shushi.pamirs.business.view.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;

import java.util.Collections;
import java.util.List;

/**
 * @author Wuxin
 * @Date 2024/7/22
 * @since 1.0
 */
@Component
public class PamirsCompanyTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "companyTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(PamirsCompany.MODEL_MODEL, TEMPLATE_NAME).setDisplayName("公司-导入导出");

        PamirsCompanyTemplate.createCompanySheet(builder);

        return Collections.singletonList(builder.build());
    }

    private static void createCompanySheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName("员工")
                .createBlock(PamirsCompany.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:F3")
                .createMergeRange("A1:F1")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("dataStatus").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000))
                .setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("DRAFT", "草稿")
                        .put("NOT_ENABLED", "未启用")
                        .put("ENABLED", "已启用")
                        .put("DISABLED", "已禁用")
                        .build()))
                .and()
                .createCell().setField("parent.code").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("parent.name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("remark").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(1500))
                .createCell().setValue("1. 红色为必填字段。\n" +
                        "2. 公司编码相同新数据直接覆盖旧数据。\n" +
                        "3. 数据状态可选项：草稿,未启用,以启用,已禁用。未填写时默认状态为已启用状态。\n" +
                        "4. 公司不能选择自身作为上级公司。")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 93, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).build()))
                        .add(new RichTextFormat(0, 11, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("公司编码")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("公司名称")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("数据状态").and()
                .createCell().setValue("上级公司编码").and()
                .createCell().setValue("上级公司名称").and()
                .createCell().setValue("备注");
    }
}
