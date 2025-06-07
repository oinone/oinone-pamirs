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
        return Collections.singletonList(WorkbookDefinitionBuilder.newInstance(ResourceCurrency.MODEL_MODEL, "货币单位")
                .createSheet().setName("货币单位")
                .createBlock(ResourceCurrency.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        0, 1, 0, 9)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").and()
                .createCell().setField("name").and()
                .createCell().setField("symbol").and()
                .createCell().setField("position").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("BEFORE", "货币符号在前")
                        .put("AFTER", "货币符号在后")
                        .build())).and()
                .createCell().setField("rounding").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("ROUND_UP", "直接向上")
                        .put("ROUND_DOWN", "直接向下")
                        .put("ROUND_CEILING", "正数进位向上，负数舍位向上")
                        .put("ROUND_FLOOR", "正数舍位向下，负数进位向下")
                        .put("ROUND_HALF_UP", "四舍五入，若舍弃部分>=.5，就进位")
                        .put("ROUND_HALF_DOWN", "四舍五入 若舍弃部分>.5")
                        .put("ROUND_HALF_EVEN", "如果舍弃部分左边的数字为偶数，则作ROUND_HALF_DOWN，如果舍弃部分左边的数字为奇数，则作 ROUND_HALF_UP")
                        .put("ROUND_UNNECESSARY", "断言请求的操作具有精确的结果，因此不需要舍入")
                        .build())).and()
                .createCell().setField("decimalPlaces").setType(ExcelValueTypeEnum.NUMBER).setFormat("0").and()
                .createCell().setField("currencyUnitLabel").and()
                .createCell().setField("currencySubunitLabel").and()
                .createCell().setField("active").setType(ExcelValueTypeEnum.BOOLEAN).and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("编码").and()
                .createCell().setValue("名称").and()
                .createCell().setValue("货币符号").and()
                .createCell().setValue("符号位置").and()
                .createCell().setValue("精确方式").and()
                .createCell().setValue("小数精度").and()
                .createCell().setValue("整数单位").and()
                .createCell().setValue("小数单位").and()
                .createCell().setValue("是否激活").and()
                .and().and()
                .createUnique(ResourceCurrency.MODEL_MODEL).addUnique("code").and()
                .and()
                .build());
    }
}
