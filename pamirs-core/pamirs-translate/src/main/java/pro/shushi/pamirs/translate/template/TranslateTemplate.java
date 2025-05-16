package pro.shushi.pamirs.translate.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;

import java.util.Collections;
import java.util.List;

/**
 * @author: xuxin
 * @createTime: 2024/05/12 14:05
 */
@Component
public class TranslateTemplate implements ExcelTemplateInit {
    public static final String TEMPLATE_NAME = "translateTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {

        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(ResourceTranslation.MODEL_MODEL, TEMPLATE_NAME).setDisplayName("导入导出翻译文件");

        TranslateTemplate.createImportTranslateSheet(builder);

        return Collections.singletonList(builder.build());
    }

    private static void createImportTranslateSheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName("翻译项")
                .createBlock(ResourceTranslationItem.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:H3")
                .createMergeRange("A1:H1")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("module").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("resLangCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("langCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("origin").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("target").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("state").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).setType(ExcelValueTypeEnum.BOOLEAN)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("true", "激活")
                        .put("false", "未激活")
                        .build()
                )).and()
                .createCell().setField("scope").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("MODULE", "源术语所在应用")
                        .put("GLOBAL", "全局")
                        .build()))
                .and()
                .createCell().setField("comments").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(1500))
                .createCell().setValue(
                        "1.源语言编码、目标语言编码通常使用ISO 639标准来表示不同的语言。\n" +
                                "例如：中文（中国大陆）: zh-CN英语（美国）: en-US\n" +
                                "2.源术语（Source Term）是指需要翻译的原始文本。\n" +
                                "3.“翻译值”指某个源术语（Source Term）被翻译成的具体目标内容，即翻译后的结果或目标术语（Target Term）。")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 164, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).build()))
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
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("应用").and()
                .createCell().setValue("源语言编码").and()
                .createCell().setValue("目标语言编码").and()
                .createCell().setValue("源术语").and()
                .createCell().setValue("翻译值").and()
                .createCell().setValue("是否激活").and()
                .createCell().setValue("翻译应用范围").and()
                .createCell().setValue("备注");
    }
}
