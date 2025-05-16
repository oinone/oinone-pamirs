package pro.shushi.pamirs.file.api.util;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.LetterHelper;
import pro.shushi.pamirs.core.common.cache.UnsafeCache;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelCellDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelHeaderDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.middleware.schedule.common.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2021-01-15 15:15
 */
public class ExcelImportErrorFileHelper {

    private static final String DEFAULT_NEW_FIELD_PREFIX = "PAMIRS_";

    private static final String DEFAULT_ERROR_MESSAGE_HEADER_VALUE = "错误信息";

    private static final int NEW_FIELD_LENGTH = 16;

    private final ExcelDefinitionContext context;

    private Integer currentBeginRowIndex;

    private Integer currentEndRowIndex;

    private Integer currentBeginColumnIndex;

    private Integer currentEndColumnIndex;

    private Map<Integer, EasyExcelBlockDefinition> blockDefinitionMap = new HashMap<>(64);

    private Map<Integer, String> errorMessageKeys;

    private ExcelImportErrorFileHelper(ExcelDefinitionContext context) {
        this.errorMessageKeys = new HashMap<>();
        this.context = context;
    }

    public static ExcelImportErrorFileHelper generatorErrorFile(ExcelDefinitionContext context) {
        return new ExcelImportErrorFileHelper(context);
    }

    public Result<List<List<Map<String, String>>>> get(List<List<Map<Integer, String>>> errorDataList) {
        Result<List<List<Map<String, String>>>> result = new Result<>();
        UnsafeCache<Integer, String> newFieldKeyCache = new UnsafeCache<>(64, columnIndex -> DEFAULT_NEW_FIELD_PREFIX + columnIndex);
        addErrorMessageColumn(result, newFieldKeyCache);
        if (!result.isSuccess()) {
            return result;
        }
        generatorErrorDataList(result, newFieldKeyCache, errorDataList);
        return result;
    }

    private void addErrorMessageColumn(Result<List<List<Map<String, String>>>> result, UnsafeCache<Integer, String> newFieldKeyCache) {
        List<EasyExcelSheetDefinition> sheetList = context.getSheetList();
        int blockIndex = 0;
        addField:
        for (EasyExcelSheetDefinition sheet : sheetList) {
            List<EasyExcelBlockDefinition> blockList = sheet.getBlockDefinitions();
            for (EasyExcelBlockDefinition block : blockList) {
                String errorMessageKey = this.errorMessageKeys.get(blockIndex);
                if (errorMessageKey == null) {
                    errorMessageKey = LetterHelper.getRandomString(NEW_FIELD_LENGTH);
                    while (this.errorMessageKeys.containsValue(errorMessageKey)) {
                        errorMessageKey = LetterHelper.getRandomString(NEW_FIELD_LENGTH);
                    }
                    this.errorMessageKeys.put(blockIndex, errorMessageKey);
                }
                this.blockDefinitionMap.put(blockIndex, block);
                addErrorMessageColumn(result, sheet, block, newFieldKeyCache, errorMessageKey);
                if (!result.isSuccess()) {
                    break addField;
                }
                blockIndex++;
            }
        }
    }

    private void addErrorMessageColumn(Result<List<List<Map<String, String>>>> result, EasyExcelSheetDefinition sheet, EasyExcelBlockDefinition block, UnsafeCache<Integer, String> newFieldKeyCache, String errorMessageKey) {
        if (block.getFieldCells().containsKey(errorMessageKey)) {
            return;
        }
        if (!ExcelAnalysisTypeEnum.FIXED_HEADER.equals(block.getAnalysisType())) {
            result.setFail("暂不支持的工作簿解析类型");
            return;
        }
        setDesignRange(block.getDesignRange());
        EasyExcelCellDefinition errorCellDefinition = new EasyExcelCellDefinition()
                .setKey(errorMessageKey)
                .setField(errorMessageKey)
                .setType(ExcelValueTypeEnum.STRING)
                .setIsStatic(true);

        Map<String, EasyExcelCellDefinition> fieldCells = block.getFieldCells();
        fieldCells.clear();
        List<TreeNode<EasyExcelCellDefinition>> fieldNodeList = block.getFieldNodeList();
        fieldNodeList.clear();
        Map<Integer, EasyExcelCellDefinition> columnFieldCells = block.getColumnFieldCells();

        columnFieldCells.put(this.currentEndColumnIndex + 1, errorCellDefinition);
        fieldCells.put(errorMessageKey, errorCellDefinition);
        fieldNodeList.add(new TreeNode<>(errorMessageKey, errorCellDefinition));

        for (Map.Entry<Integer, EasyExcelCellDefinition> columnFieldCell : columnFieldCells.entrySet()) {
            int columnIndex = columnFieldCell.getKey();
            EasyExcelCellDefinition cellDefinition = columnFieldCell.getValue();
            String newField = newFieldKeyCache.get(columnIndex);
            cellDefinition.setField(newField);
            fieldCells.put(newField, cellDefinition);
            fieldNodeList.add(new TreeNode<>(newField, cellDefinition));
        }

        List<ExcelHeaderDefinition> headerDefinitionList = block.getHeaderDefinitionList();
        if (CollectionUtils.isEmpty(headerDefinitionList)) {
            result.setFail("未找到表头行，无法生成错误信息列");
            return;
        }
        int headerHeight = headerDefinitionList.size();
        for (ExcelHeaderDefinition headerDefinition : headerDefinitionList) {
            List<ExcelCellDefinition> headerCellList = headerDefinition.getCellList();
            if (CollectionUtils.isNotEmpty(headerCellList)) {
                headerCellList.add(headerCellList.get(headerCellList.size() - 1).clone()
                        .setField(null)
                        .setValue(DEFAULT_ERROR_MESSAGE_HEADER_VALUE)
                        .setType(ExcelValueTypeEnum.STRING));
            }
        }

        ExcelHeaderDefinition configHeader = block.getConfigHeader();
        if (configHeader != null) {
            List<ExcelCellDefinition> configHeaderCellList = configHeader.getCellList();
            if (CollectionUtils.isNotEmpty(configHeaderCellList)) {
                String field = ExcelWorkbookDefinitionUtil.fieldCellKeyGenerator(block, new ExcelCellDefinition().setField(errorMessageKey), ExcelWorkbookDefinitionUtil.EASY_EXCEL_FIXED_HEADER_FILL_KEY_GENERATOR);
                ExcelCellDefinition cellDefinition = configHeaderCellList.get(configHeaderCellList.size() - 1).clone()
                        .setField(field)
                        .setValue("{" + FileConstant.BLOCK_PREFIX + block.getBlockNumber() + FileConstant.POINT_CHARACTER + field + "}")
                        .setType(ExcelValueTypeEnum.STRING);
                configHeaderCellList.add(cellDefinition);
            }
        }

        Map<Integer, ExcelStyleDefinition> columnStyles = sheet.getColumnStyles();
        if (columnStyles != null) {
            ExcelStyleDefinition styleDefinition = columnStyles.get(this.currentEndColumnIndex);
            if (styleDefinition == null) {
                for (int i = this.currentEndColumnIndex - 1; i > 0; i--) {
                    styleDefinition = columnStyles.get(i);
                    if (styleDefinition != null) {
                        break;
                    }
                }
                if (styleDefinition == null) {
                    styleDefinition = ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(true)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER).build();
                } else {
                    styleDefinition = styleDefinition.clone()
                            .setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)
                            .setWidth(null)
                            .setTypefaceDefinition(TypefaceDefinitionBuilder.newInstance().setBold(true).build());
                }
                columnStyles.put(this.currentEndColumnIndex, styleDefinition);
            }
        }

        int errorColumnIndex = this.currentEndColumnIndex + 1;
        block.getDesignRange().setEndColumnIndex(errorColumnIndex);

        if (headerHeight >= 2) {
            sheet.getMergeRangeList().add(new ExcelCellRangeDefinition()
                    .setBeginColumnIndex(errorColumnIndex)
                    .setEndColumnIndex(errorColumnIndex)
                    .setBeginRowIndex(this.currentBeginRowIndex)
                    .setEndRowIndex(this.currentBeginRowIndex + headerHeight - 1)
                    .setFixedBeginRowIndex(false)
                    .setFixedEndRowIndex(false)
                    .setFixedBeginColumnIndex(false)
                    .setFixedEndColumnIndex(false));
        }
    }

    private void setDesignRange(ExcelCellRangeDefinition designRange) {
        this.currentBeginRowIndex = designRange.getBeginRowIndex();
        this.currentEndRowIndex = designRange.getEndRowIndex();
        this.currentBeginColumnIndex = designRange.getBeginColumnIndex();
        this.currentEndColumnIndex = designRange.getEndColumnIndex();
    }

    private void generatorErrorDataList(Result<List<List<Map<String, String>>>> result, UnsafeCache<Integer, String> newFieldKeyCache, List<List<Map<Integer, String>>> errorDataList) {
        List<List<Map<String, String>>> errorResultList = new ArrayList<>();
        int blockIndex = 0;
        for (List<Map<Integer, String>> errorData : errorDataList) {
            EasyExcelBlockDefinition block = blockDefinitionMap.get(blockIndex);
            if (block == null) {
                blockIndex++;
                continue;
            }
            setDesignRange(block.getDesignRange());
            Result<List<Map<String, String>>> convertResult = convertErrorDataList(newFieldKeyCache, errorData);
            if (convertResult.isSuccess()) {
                errorResultList.add(convertResult.getData());
            } else {
                result.setFail(convertResult.getErrorMessage());
                return;
            }
            blockIndex++;
        }
        result.setData(errorResultList);
    }

    private Result<List<Map<String, String>>> convertErrorDataList(UnsafeCache<Integer, String> newFieldKeyCache, List<Map<Integer, String>> errorDataList) {
        Result<List<Map<String, String>>> result = new Result<>();
        List<Map<String, String>> list = new ArrayList<>();
        for (Map<Integer, String> errorData : errorDataList) {
            Map<String, String> resultData = new HashMap<>(errorData.size());
            for (Map.Entry<Integer, String> errorDataEntry : errorData.entrySet()) {
                resultData.put(newFieldKeyCache.get(errorDataEntry.getKey()), errorDataEntry.getValue());
            }
            list.add(resultData);
        }
        result.setData(list);
        return result;
    }
}
