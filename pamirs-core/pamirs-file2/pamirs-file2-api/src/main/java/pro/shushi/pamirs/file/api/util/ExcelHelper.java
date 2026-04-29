package pro.shushi.pamirs.file.api.util;

import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.constant.ExpressionConstant;
import pro.shushi.pamirs.file.api.builder.StyleDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.ExcelBorderStyleEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelVerticalAlignmentEnum;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Excel Helper
 *
 * @author Adamancy Zhang at 22:16 on 2021-01-14
 */
public class ExcelHelper {

    private ExcelHelper() {
        // reject create object
    }

    public static StyleDefinitionBuilder<?> createDefaultStyle() {
        return createDefaultStyle(null);
    }

    public static StyleDefinitionBuilder<?> createDefaultStyle(Consumer<TypefaceDefinitionBuilder<?>> typefaceConsumer) {
        StyleDefinitionBuilder<?> styleBuilder = new StyleDefinitionBuilder<>(null)
                .setHorizontalAlignment(ExcelHorizontalAlignmentEnum.GENERAL)
                .setVerticalAlignment(ExcelVerticalAlignmentEnum.CENTER)
                .setFillBorderStyle(ExcelBorderStyleEnum.THIN);
        TypefaceDefinitionBuilder<?> typefaceBuilder = styleBuilder.createTypeface()
                .setSize(11);
        if (typefaceConsumer != null) {
            typefaceConsumer.accept(typefaceBuilder);
        }
        return styleBuilder;
    }

    public static String translateFilename(ExcelDefinitionContext context, String filename) {
        for (ExcelTypeEnum excelType : ExcelTypeEnum.values()) {
            String suffix = excelType.getValue();
            if (filename.endsWith(suffix)) {
                return context.translate(filename.substring(0, filename.length() - suffix.length())) + suffix;
            }
        }
        return filename;
    }

    public static String generatorFilename(ExcelWorkbookDefinition workbookDefinition) {
        return generatorFilename(workbookDefinition, null);
    }

    public static String generatorFilename(ExcelWorkbookDefinition workbookDefinition, String suffix) {
        String filename = getFilename(workbookDefinition);
        if (suffix != null) {
            filename = filename.concat(suffix);
        }
        Boolean isClearExportStyle = workbookDefinition.getClearExportStyle();
        if (isClearExportStyle != null && workbookDefinition.getClearExportStyle()) {
            return filename.concat(FileConstant.CSV_FILE_SUFFIX);
        }
        return filename.concat(workbookDefinition.getVersion().getExcelType().getValue());
    }

    private static String getFilename(ExcelWorkbookDefinition workbookDefinition) {
        return Optional.ofNullable(workbookDefinition.getFilename())
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> Optional.ofNullable(workbookDefinition.getDisplayName())
                        .filter(StringUtils::isNotBlank)
                        .orElseGet(() -> Optional.ofNullable(workbookDefinition.getName())
                                .filter(StringUtils::isNotBlank)
                                .orElseGet(UUIDUtil::getUUIDNumberString)));
    }

    public static String generatorSheetName(ExcelSheetDefinition sheetDefinition, int sheetIndex) {
        return Optional.ofNullable(sheetDefinition.getName())
                .filter(StringUtils::isNotBlank)
                .orElse("Sheet " + sheetIndex);
    }

    public static ExcelFixedHeadHelper fixedHeader(String model, String name) {
        return new ExcelFixedHeadHelper(model, name);
    }

    /**
     * 创建固定表头 Helper，支持全局列宽自适应默认值。
     *
     * @param model            模型编码
     * @param name             模板名称
     * @param autoColumnWidth  全局列宽自适应默认值；仅在单元格未显式配置 {@code autoColumnWidth} 时生效，单元格配置优先
     */
    public static ExcelFixedHeadHelper fixedHeader(String model, String name, boolean autoColumnWidth) {
        return new ExcelFixedHeadHelper(model, name, autoColumnWidth);
    }

    public static String generatorSingleObjectFormatExpression(String optionLabelField) {
        return ExpressionConstant.ACTIVE_RECORD + CharacterConstants.SEPARATOR_DOT + optionLabelField;
    }

    public static String generatorMultiObjectFormatExpression(String referenceModel, String optionLabelField) {
        return String.format("CONCAT(LIST_FIELD_VALUES(%s, '%s', '%s'), ',')", ExpressionConstant.ACTIVE_RECORDS, referenceModel, optionLabelField);
    }

    public static String generatorMultiValueFormatExpression() {
        return String.format("CONCAT(%s, ',')", ExpressionConstant.ACTIVE_RECORDS);
    }

    public static String getNumberFormat(Integer decimal) {
        if (decimal == null || decimal <= -1) {
            decimal = null;
        }
        String format;
        if (decimal == null) {
            format = ExcelValueTypeEnum.NUMBER.getDefaultFormat();
        } else {
            format = String.format("0.%0" + decimal + "d", 0);
        }
        return format;
    }
}
