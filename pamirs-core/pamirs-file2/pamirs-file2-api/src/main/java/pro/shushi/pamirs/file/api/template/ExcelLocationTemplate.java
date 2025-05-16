package pro.shushi.pamirs.file.api.template;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.model.ExcelLocation;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.template.entity.ExcelLocationData;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;

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
                .setDisplayName("导入导出模板翻译")
                .setDefaultShow(Boolean.FALSE)
                .createSheet().setName("国际化配置")
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
                .createCell().setValue("模型编码").and()
                .createCell().setValue("模板名称").and()
                .createCell().setValue("模板显示名称").and()
                .createCell().setValue("源语言").and()
                .createCell().setValue("目标语言").and()
                .createCell().setValue("源术语").and()
                .createCell().setValue("翻译值").and()
                .and().and()
                .and()
                .build());
    }
}
