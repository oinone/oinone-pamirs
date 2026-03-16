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
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;

import java.util.Collections;
import java.util.List;

/**
 * @author: xuxin
 * @createTime: 2024/05/12 14:21
 */
@Component
public class ChangeTranslateTemplate implements ExcelTemplateInit {
    public static final String TEMPLATE_NAME = "ChangeTranslateTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(ResourceTranslation.MODEL_MODEL, TEMPLATE_NAME)
                .setDisplayName(I18nUtils.getMessage("file.template.translate.change.title"));

        ChangeTranslateTemplate.createChangeTranslateSheet(builder);

        return Collections.singletonList(builder.build());
    }

    private static void createChangeTranslateSheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName(I18nUtils.getMessage("file.template.translate.sheet"))
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
                        .put("true", I18nUtils.getMessage("file.template.translate.active.true"))
                        .put("false", I18nUtils.getMessage("file.template.translate.active.false"))
                        .build()
                )).and()
                .createCell().setField("scope").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("MODULE", I18nUtils.getMessage("file.template.translate.scope.module"))
                        .put("GLOBAL", I18nUtils.getMessage("file.template.translate.scope.global"))
                        .build()))
                .and()
                .createCell().setField("comments").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(1500))
                .createCell().setValue(
                        I18nUtils.getMessage("file.template.translate.tips"))
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
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.module")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.originLang")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.targetLang")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.origin")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.target")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.active")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.scope")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.translate.comments"));
    }
}
