package pro.shushi.pamirs.user.core.base.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.core.base.pmodel.PamirsUserProxy;

import java.util.Collections;
import java.util.List;

/**
 * @author: Wuxin
 * @createTime: 2024/06/11 17:29
 */
@Component
public class PamirsUserTemplate implements ExcelTemplateInit {
    public static final String TEMPLATE_NAME = "pamirsUserTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(PamirsUser.MODEL_MODEL, TEMPLATE_NAME).setDisplayName(I18nUtils.getMessage("file.template.user.title"));
        PamirsUserTemplate.createRoleTypeSheet(builder);
        return Collections.singletonList(builder.build());
    }

    public static void createRoleTypeSheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName(I18nUtils.getMessage("file.template.user.sheet"))
                .createBlock(PamirsUserProxy.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:H3")
                .createMergeRange("A1:H1")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("gender").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("NULL", I18nUtils.getMessage("file.template.user.gender.unknown"))
                        .put("MALE", I18nUtils.getMessage("file.template.user.gender.male"))
                        .put("FEMALE", I18nUtils.getMessage("file.template.user.gender.female"))
                        .build())).and()
                .createCell().setField("login").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("active").setType(ExcelValueTypeEnum.BOOLEAN).setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("initialPassword").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("contactPhone").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("contactEmail").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("roleCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and().and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(2200))
                .createCell().setValue(I18nUtils.getMessage("file.template.user.tips"))
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 234, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).build()))
                        .add(new RichTextFormat(0, 9, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
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
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue(I18nUtils.getMessage("file.template.user.name"))
                .setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setSize(18)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 2, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue(I18nUtils.getMessage("file.template.user.gender")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.user.login"))
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue(I18nUtils.getMessage("file.template.user.active")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.user.initialPassword")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.user.contactPhone")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.user.contactEmail")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.user.roleCode"));
    }

}
