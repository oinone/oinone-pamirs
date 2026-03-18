package pro.shushi.pamirs.file.api.template;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.model.ExcelLocation;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.template.entity.ExcelLocationData;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.locale.utils.I18nUtils;

import java.util.Collections;
import java.util.List;

/**
 * {@link ExcelLocation} import/export template
 *
 * @author Adamancy Zhang at 17:12 on 2024-06-01
 */
@Component
public class ExcelLocationTemplate implements ExcelTemplateInit {

    public static final String TEMPLATE_NAME = "excelLocationTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        return Collections.singletonList(WorkbookDefinitionBuilder.newInstance(ExcelWorkbookDefinition.MODEL_MODEL, TEMPLATE_NAME)
                .setType(ExcelTemplateTypeEnum.IMPORT_EXPORT)
                .setExportStrategy(ExcelExportStrategyEnum.BLOCK)
                .setDisplayName(I18nUtils.getMessage("file.template.location.title"))
                .setDefaultShow(Boolean.FALSE)
                .createSheet().setName(I18nUtils.getMessage("file.template.location.sheet.i18n"))
                .createBlock(ExcelLocationData.class.getName(), ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:G2")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("model").and()
                .createCell().setField("name").and()
                .createCell().setField("displayName").and()
                .createCell().setField("originLang").and()
                .createCell().setField("targetLang").and()
                .createCell().setField("origin").and()
                .createCell().setField("target").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue(I18nUtils.getMessage("file.template.location.header.model")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.location.header.name")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.location.header.displayName")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.location.header.originLang")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.location.header.targetLang")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.location.header.origin")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.location.header.target")).and()
                .and().and()
                .and()
                .build());
    }
}
