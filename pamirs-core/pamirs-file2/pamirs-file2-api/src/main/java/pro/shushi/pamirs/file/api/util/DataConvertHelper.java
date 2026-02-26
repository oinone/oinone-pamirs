package pro.shushi.pamirs.file.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;
import com.google.api.client.util.ArrayMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import pro.shushi.pamirs.core.common.DateHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelExportDataHolder;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelCellDefinition;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.ux.common.utils.NumberHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.*;
import java.util.*;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SO;
import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SS;

/**
 * Excel数据转换帮助类
 *
 * @author Adamancy Zhang
 * @date 2021-01-21 22:59
 */
@Slf4j
public class DataConvertHelper {

    private static final String EXCEL_NUMBER_SUFFIX = "\t";

    private static final int EXCEL_NUMBER_MAX_LENGTH = 15;

    private DataConvertHelper() {
        //reject create object
    }

    public static List<Map<String, Object>> convertDataByList(ExcelDefinitionContext definitionContext, EasyExcelBlockDefinition blockDefinition, List<Object> dataList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object data : dataList) {
            resultList.addAll(convertDataByObject(definitionContext, blockDefinition, data));
        }
        return resultList;
    }

    public static List<Map<String, Object>> convertDataByObject(ExcelDefinitionContext definitionContext, EasyExcelBlockDefinition blockDefinition, Object data) {
        ExcelExportDataHolder rootNode = new ExcelExportDataHolder(blockDefinition);
        rootNode.getResultList().add(rootNode.getCurrentRow());
        convertDataByMap(definitionContext, rootNode, getDataMap(data), true, false);
        return rootNode.getResultList();
    }

    private static void convertDataByMap(ExcelDefinitionContext definitionContext, ExcelExportDataHolder dataHolder, Map<Object, Object> data, boolean needAddCopyData, boolean needCopyRow) {
        String key, field;
        Object value;
        if (needCopyRow) {
            dataHolder.setCurrentRow(dataHolder.getParent().copy());
            dataHolder.getResultList().add(dataHolder.getCurrentRow());
        }
        List<TreeNode<EasyExcelCellDefinition>> listNode = new ArrayList<>();
        Map<String, ListNodeHolder> listValueMap = new ArrayMap<>();
        for (TreeNode<EasyExcelCellDefinition> node : dataHolder.getFieldList()) {
            EasyExcelCellDefinition cellDefinition = node.getValue();
            TreeNode<EasyExcelCellDefinition> parentNode = node.getParent();
            if (parentNode == null) {
                field = node.getKey();
            } else {
                field = node.getKey().substring(parentNode.getKey().length() + 1);
                int p = field.indexOf(FileConstant.POINT_CHARACTER);
                if (p != -1) {
                    field = field.substring(0, p);
                }
            }
            key = cellDefinition.getKey();
            int li = field.indexOf("["), ri = field.indexOf("]"), index = -1;
            if (li != -1 && ri != -1) {
                index = Integer.parseInt(field.substring(li + 1, ri));
                field = field.substring(0, li);
            }
            value = data.get(field);
            if (value == null) {
                value = "";
            }
            if (node.isLeaf()) {
                value = convertExportValue(definitionContext, cellDefinition, data, value);
                dataHolder.putCurrentRowValue(key, value);
                if (needAddCopyData) {
                    dataHolder.putNeedCopyValue(key, value);
                }
            } else {
                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    if (list.isEmpty()) {
                        continue;
                    }
                    listNode.add(node);
                    ListNodeHolder listNodeHolder = new ListNodeHolder();
                    listNodeHolder.key = key;
                    listNodeHolder.index = index;
                    listNodeHolder.field = field;
                    listNodeHolder.value = list;
                    listValueMap.put(node.getKey(), listNodeHolder);
                } else {
                    convertDataByMap(definitionContext, new ExcelExportDataHolder(node.getChildren(), dataHolder), getDataMap(value), true, false);
                }
            }
        }
        for (TreeNode<EasyExcelCellDefinition> node : listNode) {
            ListNodeHolder listNodeHolder = listValueMap.get(node.getKey());
            int index = listNodeHolder.index;
            List<?> list = listNodeHolder.value;
            if (index != -1 && index < list.size()) {
                Object itemValue = list.get(index);
                if (itemValue == null) {
                    return;
                }
                ExcelExportDataHolder nextDataHolder = new ExcelExportDataHolder(node.getChildren(), dataHolder);
                convertDataByMap(definitionContext, nextDataHolder, getDataMap(itemValue), false, false);
            } else {
                boolean needCopyRow0 = false;
                ExcelExportDataHolder nextDataHolder = new ExcelExportDataHolder(node.getChildren(), dataHolder);
                nextDataHolder.resetIterator();
                for (Object item : list) {
                    convertDataByMap(definitionContext, nextDataHolder, getDataMap(item), false, needCopyRow0);
                    if (nextDataHolder.nextRow()) {
                        needCopyRow0 = false;
                    } else {
                        needCopyRow0 = true;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Object convertExportValue(ExcelDefinitionContext definitionContext, EasyExcelCellDefinition cellDefinition, Map<Object, Object> data, Object value) {
        ExcelValueTypeEnum type = cellDefinition.getType();
        if (type == null) {
            type = ExcelValueTypeEnum.STRING;
        }
        if (ExcelValueTypeEnum.OBJECT.equals(type)) {
            return convertExportObjectValue(definitionContext, cellDefinition, data, value);
        }
        String stringValue = StringHelper.valueOf(value, StringHelper.Feature.ENUMERATION_TO_VALUE);
        boolean isSetValue = StringUtils.isNotBlank(stringValue);
        Object formatObject = fetchExportFormat(definitionContext, cellDefinition);
        switch (type) {
            case INTEGER:
            case NUMBER:
                if (isSetValue) {
                    value = NumberHelper.valueOf(value);
                    NumberFormat numberFormat = (NumberFormat) formatObject;
                    stringValue = numberFormat.format(value);
                    if (stringValue.length() <= EXCEL_NUMBER_MAX_LENGTH) {
                        return value;
                    }
                    return stringValue + EXCEL_NUMBER_SUFFIX;
                }
                break;
            case BOOLEAN:
            case ENUMERATION:
                Map<String, String> enumerationMap = (Map<String, String>) formatObject;
                if (enumerationMap == null) {
                    if (value instanceof Enum) {
                        if (value instanceof IEnum) {
                            return ((IEnum<?>) value).displayName();
                        } else {
                            return StringHelper.valueOf(value);
                        }
                    }
                } else {
                    if (isSetValue) {
                        String tempValue = enumerationMap.get(stringValue);
                        if (tempValue == null) {
                            if (!(value instanceof String)) {
                                value = null;
                            }
                        } else {
                            value = tempValue;
                        }
                        return value;
                    }
                }
                break;
            case DATETIME:
                if (value instanceof String) {
                    return value;
                }
                return ((DateFormat) formatObject).format((Date) value);
            case STRING:
            case FORMULA:
            case CALENDAR:
            case COMMENT:
            case HYPER_LINK:
            case RICH_TEXT_STRING: {
                if (Boolean.TRUE.equals(cellDefinition.getTranslate())) {
                    stringValue = definitionContext.translate(stringValue);
                }
                return stringValue;
            }
        }
        return value;
    }

    private static Object convertExportObjectValue(ExcelDefinitionContext definitionContext, EasyExcelCellDefinition cellDefinition, Map<Object, Object> data, Object value) {
        Object formatObject = fetchExportFormat(definitionContext, cellDefinition);
        if (formatObject != null && !CharacterConstants.SEPARATOR_EMPTY.equals(value)) {
            Map<String, Object> context = generatorExpContext(data, value);
            try {
                return Exp.fastRun((String) formatObject, ScriptType.EL, context);
            } catch (Throwable e) {
                if (formatObject instanceof String) {
                    log.error("execute expression error. expression: {}", formatObject, e);
                } else {
                    log.error("execute expression error.", e);
                }
            }
        }
        return CharacterConstants.SEPARATOR_EMPTY;
    }

    /**
     * 构造表达式执行上下文
     *
     * @param data  当前行数据（或当前对象数据）
     * @param value 当前字段数据
     * @return 表达式执行上下文
     */
    private static Map<String, Object> generatorExpContext(Map<Object, Object> data, Object value) {
        Map<String, Object> context = new HashMap<>();
        context.put("parentRecord", data);
        if (value instanceof D) {
            value = ((D) value).get_d();
        }
        if (value instanceof Map) {
            context.put("activeRecord", value);
            context.put("activeRecords", Lists.newArrayList(value));
        } else if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            context.put("activeRecords", coll);
            if (coll.isEmpty()) {
                context.put("activeRecord", null);
            } else {
                context.put("activeRecord", coll.iterator().next());
            }
        } else {
            context.put("activeRecord", data);
            context.put("activeRecords", Lists.newArrayList(data));
        }
        return context;
    }

    private static Object fetchExportFormat(ExcelDefinitionContext definitionContext, EasyExcelCellDefinition cellDefinition) {
        boolean isFormatInit = cellDefinition.getIsFormatInit();
        if (!isFormatInit) {
            ExcelValueTypeEnum valueType = cellDefinition.getType();
            String format = cellDefinition.getFormat();
            boolean isFormat = StringUtils.isNotBlank(format);
            switch (valueType) {
                case INTEGER:
                    DecimalFormat integerFormat = new DecimalFormat();
                    integerFormat.setRoundingMode(RoundingMode.DOWN);
                    if (isFormat) {
                        integerFormat.applyLocalizedPattern(format);
                    } else {
                        integerFormat.applyLocalizedPattern(valueType.getDefaultFormat());
                    }
                    cellDefinition.setFormatObject(integerFormat);
                    break;
                case NUMBER:
                    DecimalFormat numberFormat = new DecimalFormat();
                    numberFormat.setRoundingMode(RoundingMode.HALF_UP);
                    if (isFormat) {
                        numberFormat.applyLocalizedPattern(format);
                    } else {
                        numberFormat.applyLocalizedPattern(valueType.getDefaultFormat());
                    }
                    cellDefinition.setFormatObject(numberFormat);
                    break;
                case BOOLEAN:
                case ENUMERATION:
                    Map<String, String> enumerationMap;
                    if (isFormat) {
                        enumerationMap = JSON.parseObject(format, TR_MAP_SS.getType(), Feature.OrderedField);
                        enumerationMap.replaceAll((k, v) -> definitionContext.translate(v));
                    } else {
                        enumerationMap = new LinkedHashMap<>(2);
                    }
                    cellDefinition.setFormatObject(enumerationMap);
                    break;
                case DATETIME:
                    if (!isFormat) {
                        format = valueType.getDefaultFormat();
                    }
                    cellDefinition.setFormatObject(new SimpleDateFormat(format));
                    break;
                case OBJECT:
                    if (isFormat) {
                        cellDefinition.setFormatObject(format);
                    }
                    break;
            }
            cellDefinition.setIsFormatInit(true);
        }
        return cellDefinition.getFormatObject();
    }

    private static Map<Object, Object> getDataMap(Object data) {
        if (data instanceof D) {
            //noinspection unchecked
            return (Map<Object, Object>) (Object) ((D) data).get_d();
        } else if (data instanceof Map) {
            //noinspection unchecked
            return (Map<Object, Object>) data;
        } else {
            if (data instanceof String) {
                if (StringUtils.isBlank((String) data)) {
                    return new HashMap<>();
                }
            }
            return JSON.parseObject(JSON.toJSONString(data), TR_MAP_SO.getType());
        }
    }

    public static void setCellValue(Workbook workbook, Sheet sheet, ExcelCellDefinition cellDefinition, Cell cell, CellStyle cellStyle, boolean isConfig, boolean isHeader, boolean ignoredFormat) {
        boolean isSetValue = true;
        String value = cellDefinition.getValue();
        if (value == null) {
            isSetValue = false;
        } else {
            value = value.trim();
            if ("".equals(value)) {
                isSetValue = false;
            }
        }
        ExcelValueTypeEnum type = cellDefinition.getType();
        if (type == null) {
            cell.setCellValue(value);
            return;
        }
        String format = cellDefinition.getFormat();
        boolean isFormat = StringUtils.isNotBlank(format);
        try {
            switch (type) {
                case INTEGER:
                    if (isFormat || isConfig) {
                        if (format == null) {
                            format = type.getDefaultFormat();
                        }
                        cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
                    }
                    if (isSetValue) {
                        if (isConfig || isHeader || ignoredFormat) {
                            cell.setCellValue(value);
                        } else {
                            cell.setCellValue(new BigDecimal(value).longValue());
                        }
                    }
                    break;
                case NUMBER:
                    if (isFormat || isConfig) {
                        if (format == null) {
                            format = type.getDefaultFormat();
                        }
                        cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
                    }
                    if (isSetValue) {
                        if (isConfig || isHeader || ignoredFormat) {
                            cell.setCellValue(value);
                        } else {
                            cell.setCellValue(new BigDecimal(value).doubleValue());
                        }
                    }
                    break;
                case DATETIME:
                    if (isFormat || isConfig) {
                        if (format == null) {
                            format = type.getDefaultFormat();
                        }
                        cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
                    }
                    if (isSetValue) {
                        if (isConfig || isHeader || ignoredFormat) {
                            cell.setCellValue(value);
                        } else {
                            cell.setCellValue(DateHelper.parse(value, type.getDefaultFormat()));
                        }
                    }
                    break;
                case FORMULA:
                    if (isSetValue) {
                        cell.setCellFormula(value);
                    }
                    break;
                case RICH_TEXT_STRING:
                    if (isFormat) {
                        List<RichTextFormat> richTextFormats = JSONArray.parseArray(format, RichTextFormat.class);
                        RichTextString richTextString = WorkbookHelper.createRichTextString(workbook, value);
                        for (RichTextFormat richTextFormat : richTextFormats) {
                            richTextString.applyFont(richTextFormat.getStartIndex(), richTextFormat.getEndIndex(), ExcelDefinitionConverter.convertTypeface(workbook, richTextFormat.getTypeface()));
                        }
                        cell.setCellValue(richTextString);
                        isSetValue = false;
                    }
                    if (isSetValue) {
                        cell.setCellValue(value);
                    }
                    break;
                case BOOLEAN:
                case ENUMERATION:
                    Map<String, String> enumerationMap;
                    if (isFormat) {
                        enumerationMap = JSON.parseObject(format, TR_MAP_SS.getType(), Feature.OrderedField);
                        CellRangeAddressList regions = new CellRangeAddressList(cell.getRowIndex(), cell.getRowIndex(), cell.getColumnIndex(), cell.getColumnIndex());
                        sheet.addValidationData(WorkbookHelper.createDataValidation(workbook, sheet, regions, enumerationMap.values()));
                    } else {
                        enumerationMap = new LinkedHashMap<>(2);
                    }
                    if (isSetValue) {
                        if (isConfig || isHeader || ignoredFormat) {
                            cell.setCellValue(value);
                        } else {
                            cell.setCellValue(enumerationMap.get(value));
                        }
                    }
                    break;
                case CALENDAR://暂不支持
                case COMMENT://暂不支持
                case HYPER_LINK://暂不支持
                    if (isSetValue) {
                        cell.setCellValue(value);
                    }
                    break;
                case STRING:
                    DataFormat dataFormat = workbook.createDataFormat();
                    cellStyle.setDataFormat(dataFormat.getFormat("@"));
                    cell.setCellStyle(cellStyle);
                    if (isSetValue) {
                        cell.setCellValue(value);
                    }
                default:
                    if (isSetValue) {
                        cell.setCellValue(value);
                    }
            }
        } catch (ParseException e) {
            cell.setCellValue(value);
        }
    }

    private static class ListNodeHolder {

        private String key;

        private String field;

        private Integer index;

        private List<?> value;
    }
}
