package pro.shushi.pamirs.file.api.template;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;

import java.util.Collections;
import java.util.List;

/**
 * {@link ResourceCurrency}导入/导出模板
 *
 * @author Adamancy Zhang on 2021-06-08 15:47
 */
@Component
public class ResourceCurrencyTemplate implements ExcelTemplateInit {

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        return Collections.singletonList(WorkbookDefinitionBuilder.newInstance(ResourceCurrency.MODEL_MODEL, "ResourceCurrencyTemplate")
                .setDisplayName(I18nUtils.getMessage("file.template.currency.workbook.currency"))
                .createSheet().setName(I18nUtils.getMessage("file.template.currency.sheet.currency"))
                .createBlock(ResourceCurrency.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        0, 1, 0, 9)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").and()
                .createCell().setField("name").and()
                .createCell().setField("symbol").and()
                .createCell().setField("position").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("BEFORE", I18nUtils.getMessage("file.template.currency.position.before"))
                        .put("AFTER", I18nUtils.getMessage("file.template.currency.position.after"))
                        .build())).and()
                .createCell().setField("rounding").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("ROUND_UP", I18nUtils.getMessage("file.template.currency.rounding.up"))
                        .put("ROUND_DOWN", I18nUtils.getMessage("file.template.currency.rounding.down"))
                        .put("ROUND_CEILING", I18nUtils.getMessage("file.template.currency.rounding.ceiling"))
                        .put("ROUND_FLOOR", I18nUtils.getMessage("file.template.currency.rounding.floor"))
                        .put("ROUND_HALF_UP", I18nUtils.getMessage("file.template.currency.rounding.halfUp"))
                        .put("ROUND_HALF_DOWN", I18nUtils.getMessage("file.template.currency.rounding.halfDown"))
                        .put("ROUND_HALF_EVEN", I18nUtils.getMessage("file.template.currency.rounding.halfEven"))
                        .put("ROUND_UNNECESSARY", I18nUtils.getMessage("file.template.currency.rounding.unnecessary"))
                        .build())).and()
                .createCell().setField("decimalPlaces").setType(ExcelValueTypeEnum.NUMBER).setFormat("0").and()
                .createCell().setField("currencyUnitLabel").and()
                .createCell().setField("currencySubunitLabel").and()
                .createCell().setField("active").setType(ExcelValueTypeEnum.BOOLEAN).and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.code")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.name")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.symbol")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.position")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.rounding")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.decimalPlaces")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.currencyUnitLabel")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.currencySubunitLabel")).and()
                .createCell().setValue(I18nUtils.getMessage("file.template.currency.header.active")).and()
                .and().and()
                .createUnique(ResourceCurrency.MODEL_MODEL).addUnique("code").and()
                .and()
                .build());
    }
}
